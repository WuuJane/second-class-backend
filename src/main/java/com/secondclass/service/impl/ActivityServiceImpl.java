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
                throw new RuntimeException("发生活动失败：您所在的组织未绑定上级审核单位！");
            }
        } else {
            throw new RuntimeException("发生活动失败：无法获取当前发布者的组织归属信息！");
        }

        activity.setActivityStatus("等待审核");
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
        if (record == null) return "您尚未报名该活动，无法签到！";
        if (record.getSignStatus() != null && record.getSignStatus() == 1) return "您已经签到过了，请勿重复签到！";

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

        String newStatus = isPass ? "待报名" : "活动取消";
        activityMapper.updateStatus(activityId, newStatus);
    }

    // 🌟 新增功能实现：获取负责人自己发布的活动
    @Override
    public List<Activity> getMyManageActivities(String managerId) {
        return activityMapper.selectByManagerId(managerId);
    }

    // 🌟 新增功能实现：获取某个活动的报名名单明细
    @Override
    public List<Map<String, Object>> getActivityEnrollList(String activityId) {
        return activityRecordMapper.selectStudentDetailsByActivityId(activityId);
    }
}