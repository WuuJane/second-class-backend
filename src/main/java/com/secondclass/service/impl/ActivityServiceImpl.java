package com.secondclass.service.impl;

import java.time.LocalDateTime;
import java.util.List;
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

    // 🌟 注入我们需要的新工具
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

        // 1. 根据前端传来的负责人ID，查出他具体属于哪个社团/组织
        TUser manager = userService.getUserById(activity.getManagerId());

        if (manager != null && manager.getOrgId() != null) {
            // 记下：这个活动是这个组织发起的
            activity.setManagerOrgId(manager.getOrgId());

            // 2. 去 sys_organization 表查这个社团归谁管（找大哥）
            SysOrganization org = sysOrganizationMapper.selectById(manager.getOrgId());
            if (org != null && org.getAuditorOrgId() != null) {
                // 3. 动态绑定！把大哥的 ID 强行塞进活动的审核字段里
                activity.setAuditorOrgId(org.getAuditorOrgId());
            } else {
                throw new RuntimeException("发生活动失败：您所在的组织未绑定上级审核单位！");
            }
        } else {
            throw new RuntimeException("发生活动失败：无法获取当前发布者的组织归属信息！");
        }

        // 强制初始化状态，防止前端乱传
        activity.setActivityStatus("等待审核");
        // ==========================================

        activityMapper.insert(activity);
    }

    @Override
    public String enrollActivity(String studentId, String activityId) {
        // ... (保持你原有的逻辑不变)
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
        // ... (保持你原有的逻辑不变)
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
}