package com.secondclass.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.secondclass.entity.ActivityRecord;
import com.secondclass.dto.ActivityCreateDTO;
import com.secondclass.entity.Activity;
import com.secondclass.entity.TUser;
import com.secondclass.entity.SysOrganization;
import com.secondclass.mapper.ActivityMapper;
import com.secondclass.mapper.ActivityRecordMapper;
import com.secondclass.mapper.StudentMapper;
import com.secondclass.mapper.SysOrganizationMapper;
import com.secondclass.service.ActivityService;
import com.secondclass.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;

import com.secondclass.dto.ImportResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityRecordMapper activityRecordMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(ActivityCreateDTO dto) {
        Activity activity = new Activity();
        BeanUtils.copyProperties(dto, activity);

        String uuid = UUID.randomUUID().toString().replace("-", "");
        activity.setActivityId(uuid);

        TUser manager = userService.getUserById(activity.getManagerId());

        if (manager != null && manager.getOrgId() != null) {
            activity.setManagerOrgId(manager.getOrgId());
            SysOrganization org = sysOrganizationMapper.selectById(manager.getOrgId());
            if (org != null && org.getAuditorOrgId() != null) {
                activity.setAuditorOrgId(org.getAuditorOrgId());
            } else {
                throw new RuntimeException("发布活动失败：您所在的组织未绑定上级审核单位！");
            }
        } else {
            throw new RuntimeException("发布活动失败：无法获取当前发布者的组织归属信息！");
        }

        // 🌟 修改点 1：尊重原版设计，发布后的初始状态设为"等待初审"
        activity.setActivityStatus("等待初审");
        activityMapper.insert(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditActivity(String activityId, boolean isPass, String rejectReason) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) throw new RuntimeException("活动不存在");

        String currentStatus = activity.getActivityStatus();
        String newStatus = "被驳回"; // 默认只要是驳回，都退回给负责人修改

        if (isPass) {
            // 通过时清除驳回原因
            activityMapper.updateRejectReason(activityId, null);
            // 根据当前所处的阶段，推导下一个状态
            if ("等待初审".equals(currentStatus)) {
                // 如果有终审机制，初审过了就是待终审
                newStatus = "待终审";
            } else if ("待终审".equals(currentStatus)) {
                // 终审过了，才正式向学生开放报名
                newStatus = "待报名";
            } else if ("活动结束".equals(currentStatus)) {
                // 完结审核过了，活动生命周期彻底结束
                newStatus = "活动完结";

                // =========================================================
                // 🌟 核心新增：只有在这里（活动完结时），才统一给签过到的学生发学时
                // =========================================================
                if (activity.getActivityHour() != null) {
                    // 查询该活动所有已经签到的学生
                    List<Map<String, Object>> signedInStudents = activityRecordMapper.selectSignedInStudentsByActivityId(activityId);
                    for (Map<String, Object> studentMap : signedInStudents) {
                        // 取出学生的学号
                        String sid = studentMap.containsKey("student_id") ? studentMap.get("student_id").toString() :
                                (studentMap.containsKey("studentId") ? studentMap.get("studentId").toString() : null);

                        if (sid != null) {
                            // 给该学生发放对应的学时
                            studentMapper.addHour(sid, activity.getHourType(), activity.getActivityHour());
                        }
                    }
                }
                // =========================================================

            } else {
                throw new RuntimeException("当前状态不可审核");
            }
        }

        activityMapper.updateStatus(activityId, newStatus);

        // 驳回时保存驳回原因
        if (!isPass && rejectReason != null && !rejectReason.trim().isEmpty()) {
            activityMapper.updateRejectReason(activityId, rejectReason.trim());
        }
    }

    @Override
    public List<Activity> getMyManageActivities(String managerId) {
        return activityMapper.selectByManagerId(managerId);
    }

    @Override
    public List<Map<String, Object>> getActivityEnrollList(String activityId) {
        return activityRecordMapper.selectStudentDetailsByActivityId(activityId);
    }

    @Override
    public void cancelActivity(String activityId, String managerId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        if (!activity.getManagerId().equals(managerId)) {
            throw new RuntimeException("越权操作：您无权撤销其他负责人的活动！");
        }

        // 🌟 修改点 2：只要活动还在审核阶段（初审或终审）或者被驳回了，负责人都可以撤销
        if (!"等待初审".equals(activity.getActivityStatus()) &&
                !"待终审".equals(activity.getActivityStatus()) &&
                !"被驳回".equals(activity.getActivityStatus())) {
            throw new RuntimeException("当前活动状态不允许撤销（可能已通过审核并开始报名）！");
        }

        activityMapper.updateStatus(activityId, "已撤销");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editAndResubmit(Activity activity) {
        Activity existingActivity = activityMapper.selectById(activity.getActivityId());
        if (existingActivity == null) {
            throw new RuntimeException("抱歉，未找到该活动记录！");
        }

        if (!"被驳回".equals(existingActivity.getActivityStatus())) {
            throw new RuntimeException("非法操作：只有处于“被驳回”状态的活动才可以修改和重新提交！");
        }

        activity.setActivityStatus("等待初审");
        activityMapper.updateActivity(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String signActivity(String studentId, String activityId, String signCode) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) return "活动不存在！";

        if (!"活动进行中".equals(activity.getActivityStatus())) {
            return "活动未开始或已结束，无法签到！";
        }

        // 🔥 核心修改：如果是签到码签到，进行严格的字符串比对验证
        if ("签到码".equals(activity.getSignType())) {
            if (signCode == null || !signCode.equals(activity.getSignCode())) {
                return "签到码错误，请重新核对后输入！";
            }
        }

        ActivityRecord record = activityRecordMapper.selectByActivityIdAndStudentId(activityId, studentId);
        if (record == null) return "该学生尚未报名该活动，无法签到！";
        if (record.getSignStatus() != null && record.getSignStatus() == 1) return "您已经签到过了，请勿重复操作！";

        // 更新签到状态
        activityRecordMapper.updateSignStatus(record.getId());

        // 此时不发学时，学时统一在 auditActivity 完结时发放
        return "success";
    }

    // 添加悲观锁，防止并发访问
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String enrollActivity(String studentId, String activityId) {
        // 🌟 1. 改用加了悲观锁的查询方法，谁先查到谁锁住这一行！
        Activity activity = activityMapper.selectByIdForUpdate(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        // 2. 时间校验（防作弊）
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getEnrollStartTime())) {
            throw new RuntimeException("报名还未开始！");
        }
        if (now.isAfter(activity.getEnrollEndTime())) {
            throw new RuntimeException("报名已经结束！");
        }

        // 3. 查重校验：不能重复报名
        int isEnrolled = activityRecordMapper.checkEnrolled(activityId, studentId);
        if (isEnrolled > 0) {
            throw new RuntimeException("您已经报名过该活动，请勿重复报名！");
        }

        // 4. 并发核心校验：实时统计已报名人数
        int enrolledCount = activityRecordMapper.countByActivityId(activityId);
        if (enrolledCount >= activity.getCapacity()) {
            throw new RuntimeException("手慢了，活动名额已满！");
        }

        // 5. 插入报名记录
        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setStudentId(studentId);
        activityRecordMapper.insert(record);

        return "success";
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityMapper.selectAll();
    }

    @Override
    public List<Map<String, Object>> getActualAttendanceList(String activityId) {
        return activityRecordMapper.selectSignedInStudentsByActivityId(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishActivity(String activityId, String managerId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在！");
        }

        if (!activity.getManagerId().equals(managerId)) {
            throw new RuntimeException("越权操作：您无权结束其他人的活动！");
        }

        if (!"活动进行中".equals(activity.getActivityStatus())) {
            throw new RuntimeException("当前活动状态不是“活动进行中”，无法结束！");
        }

        activityMapper.updateStatus(activityId, "活动结束");
    }

    @Override
    public Activity getActivityById(String activityId) {
        return activityMapper.getActivityById(activityId);
    }

    @Override
    public List<Activity> getMyEnrolledActivities(String studentId) {
        return activityMapper.getMyEnrolledActivities(studentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelEnroll(String studentId, String activityId) {
        Activity activity = activityMapper.getActivityById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        if (LocalDateTime.now().isAfter(activity.getStartTime())) {
            throw new RuntimeException("活动已开始，无法取消报名");
        }

        int rows = activityMapper.deleteEnrollRecord(studentId, activityId);
        if (rows == 0) {
            throw new RuntimeException("未找到你的报名记录，可能已经取消过了");
        }
    }

    @Override
    public List<Activity> getHistoryActivities(String studentId) {
        return activityMapper.getHistoryActivities(studentId);
    }

    // =========================================================================
    // 🔥 以下为补充的负责人特权操作方法（确保前台弹窗内名单的添加、签到撤销按钮不报错）
    // =========================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void managerAddStudent(String studentId, String activityId) {
        // 校验学号真实性
        if (studentMapper.selectById(studentId) == null) {
            throw new RuntimeException("学号 " + studentId + " 不存在，请核对后重新输入！");
        }
        // 查重
        int isEnrolled = activityRecordMapper.checkEnrolled(activityId, studentId);
        if (isEnrolled > 0) {
            throw new RuntimeException("该学生已经在名单中了！");
        }
        // 校验活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在！");
        }
        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setStudentId(studentId);
        activityRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualSign(String studentId, String activityId) {
        ActivityRecord record = activityRecordMapper.selectByActivityIdAndStudentId(activityId, studentId);
        if (record == null) {
            throw new RuntimeException("该学生尚未报名该活动！");
        }
        if (record.getSignStatus() != null && record.getSignStatus() == 1) {
            throw new RuntimeException("该学生已经签到过了！");
        }
        // 更新签到状态为 1
        activityRecordMapper.updateSignStatus(record.getId());

        // 容错处理：如果负责人是在【活动完结后】进行的补签，由于统一发分流程已经过去，此时需要给学生单独补发学时
        Activity activity = activityMapper.selectById(activityId);
        if (activity != null && "活动完结".equals(activity.getActivityStatus()) && activity.getActivityHour() != null) {
            studentMapper.addHour(studentId, activity.getHourType(), activity.getActivityHour());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSign(String studentId, String activityId) {
        ActivityRecord record = activityRecordMapper.selectByActivityIdAndStudentId(activityId, studentId);
        if (record == null || record.getSignStatus() == 0) {
            throw new RuntimeException("该学生尚未签到！");
        }
        // 还原签到状态为 0
        activityRecordMapper.updateSignStatusToZero(record.getId());

        // 容错处理：如果负责人是在【活动完结后】进行撤销签到，需要同步扣除已经发给该学生的学时
        Activity activity = activityMapper.selectById(activityId);
        if (activity != null && "活动完结".equals(activity.getActivityStatus()) && activity.getActivityHour() != null) {
            studentMapper.addHour(studentId, activity.getHourType(), activity.getActivityHour().negate());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importStudentsFromExcel(MultipartFile file, String activityId) {
        ImportResultDTO result = new ImportResultDTO();
        List<String> errors = new ArrayList<>();

        // 校验活动
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            result.getErrors().add("活动不存在！");
            result.setFail(1);
            return result;
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int total = 0, success = 0;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 读取第一列学号
                Cell cell = row.getCell(0);
                if (cell == null) continue;

                String studentId;
                if (cell.getCellType() == CellType.NUMERIC) {
                    studentId = String.valueOf((long) cell.getNumericCellValue());
                } else if (cell.getCellType() == CellType.STRING) {
                    studentId = cell.getStringCellValue().trim();
                } else {
                    continue;
                }

                if (studentId.isEmpty()) continue;
                total++;

                try {
                    managerAddStudent(studentId, activityId);
                    success++;
                } catch (RuntimeException e) {
                    errors.add("第" + (i + 1) + "行 " + studentId + ": " + e.getMessage());
                }
            }

            result.setTotal(total);
            result.setSuccess(success);
            result.setFail(total - success);
            result.setErrors(errors);
        } catch (Exception e) {
            result.getErrors().add("文件解析失败：" + e.getMessage());
            result.setFail(result.getTotal() + 1);
        }

        return result;
    }
}