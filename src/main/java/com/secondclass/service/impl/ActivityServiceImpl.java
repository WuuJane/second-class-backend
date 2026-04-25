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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

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
    public List<Activity> getAllActivities() {
        return activityMapper.selectAll();
    }

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
    public String enrollActivity(String studentId, String activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) return "活动不存在";

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getEnrollStartTime())) return "报名还未开始！";
        if (now.isAfter(activity.getEnrollEndTime())) return "报名已经结束！";

        int enrolledCount = activityRecordMapper.countByActivityId(activityId);
        if (enrolledCount >= activity.getCapacity()) return "手慢了，活动名额已满！";

        int isEnrolled = activityRecordMapper.checkEnrolled(activityId, studentId);
        if (isEnrolled > 0) return "您已经报名过该活动，请勿重复报名！";

        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setStudentId(studentId);
        activityRecordMapper.insert(record);

        return "success";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String signActivity(String studentId, String activityId) {
        ActivityRecord record = activityRecordMapper.selectByActivityIdAndStudentId(activityId, studentId);
        if (record == null) return "该学生尚未报名该活动，无法签到！";
        if (record.getSignStatus() != null && record.getSignStatus() == 1) return "该学生已经签到过了，请勿重复操作！";

        activityRecordMapper.updateSignStatus(record.getId());

        Activity activity = activityMapper.selectById(activityId);
        if (activity != null && activity.getActivityHour() != null) {
            studentMapper.addHour(studentId, activity.getHourType(), activity.getActivityHour());
        }

        return "success";
    }

    @Override
    public void auditActivity(String activityId, boolean isPass) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) throw new RuntimeException("活动不存在");

        // 注意：这里的状态流转我们后续开发"审核老师"功能时再做细化（区分初审通过和终审通过）
        String newStatus = isPass ? "待报名" : "被驳回";
        activityMapper.updateStatus(activityId, newStatus);
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
        // 1. 查出数据库里现有的活动记录
        Activity existingActivity = activityMapper.selectById(activity.getActivityId());

        // 2. 防御性编程：判断活动是否存在
        if (existingActivity == null) {
            throw new RuntimeException("抱歉，未找到该活动记录！");
        }

        // 3. 核心状态校验：如果不是“被驳回”状态，绝对不允许修改
        if (!"被驳回".equals(existingActivity.getActivityStatus())) {
            throw new RuntimeException("非法操作：只有处于“被驳回”状态的活动才可以修改和重新提交！");
        }

        // 4. 重置状态为“等待初审” (进入下一轮审批)
        activity.setActivityStatus("等待初审");

        // 5. 执行更新
        activityMapper.updateActivity(activity);
    }
    @Override
    public List<Map<String, Object>> getActualAttendanceList(String activityId) {
        // 调用 Mapper 层专门查询已签到学生的方法
        return activityRecordMapper.selectSignedInStudentsByActivityId(activityId);
    }
    /**
     * 负责人：手动结束活动（相当于发起完结申请）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishActivity(String activityId, String managerId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在！");
        }

        // 权限校验
        if (!activity.getManagerId().equals(managerId)) {
            throw new RuntimeException("越权操作：您无权结束其他人的活动！");
        }

        // 状态校验：只有“活动进行中”的活动才可以结束
        // (如果你允许到了时间不管开没开始都能结束，可以把逻辑放宽)
        if (!"活动进行中".equals(activity.getActivityStatus())) {
            throw new RuntimeException("当前活动状态不是“活动进行中”，无法结束！");
        }

        // 将状态更为“活动结束”，等待审核人完结并自动发学时
        activityMapper.updateStatus(activityId, "活动结束");
    }
}

    @Override
    public List<Activity> getToAuditList(String auditorOrgId) {
        // 查询本组织负责审核的，且状态为“等待初审”或“待终审”的活动
        return activityMapper.selectToAudit(auditorOrgId);
    }

    @Override
    public List<Activity> getToSettleList(String auditorOrgId) {
        // 查询状态为“活动结束”的活动
        return activityMapper.selectToSettle(auditorOrgId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditActivity(String activityId, String status) {
        // 直接更新状态：被驳回、待终审 或 待报名
        activityMapper.updateStatus(activityId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settleActivity(String activityId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) throw new RuntimeException("活动不存在");
        if (!"活动结束".equals(activity.getActivityStatus())) {
            throw new RuntimeException("操作失败：只有“活动结束”的活动才能进行结算！");
        }

        // 1. 核心步骤：给所有【已签到】的学生发放学时
        // 找出该活动下所有 sign_status = 1 的学生 ID
        List<String> signedStudentIds = activityRecordMapper.selectSignedStudentIds(activityId);

        if (signedStudentIds != null && !signedStudentIds.isEmpty()) {
            for (String studentId : signedStudentIds) {
                // 调用你之前的 studentMapper.addHour 逻辑
                studentMapper.addHour(studentId, activity.getHourType(), activity.getActivityHour());
            }
        }

        // 2. 将活动状态改为“活动完结”
        activityMapper.updateStatus(activityId, "活动完结");
    }