package com.secondclass.service.impl;

import java.time.LocalDateTime;
import com.secondclass.entity.ActivityRecord;
import com.secondclass.dto.ActivityCreateDTO;
import com.secondclass.entity.Activity;
import com.secondclass.mapper.ActivityMapper;
import com.secondclass.service.ActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.secondclass.mapper.ActivityRecordMapper;
import org.springframework.transaction.annotation.Transactional;
import com.secondclass.mapper.StudentMapper;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<Activity> getAllActivities() {
        return activityMapper.selectAll();
    }

    @Override
    public void createActivity(ActivityCreateDTO dto) {
        Activity activity = new Activity();
        // 神器：一键把 DTO 里同名的属性拷贝到 Entity 里，不用手动一个个 set！
        BeanUtils.copyProperties(dto, activity);

        // 自动生成一个 32 位的 UUID 作为主键，去掉中间的横杠
        String uuid = UUID.randomUUID().toString().replace("-", "");
        activity.setActivityId(uuid);

        // 调用 Mapper 保存到数据库
        activityMapper.insert(activity);
    }

    @Autowired
    private ActivityRecordMapper activityRecordMapper;

    @Override
    public String enrollActivity(String studentId, String activityId) {
        // 1. 查询活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return "活动不存在";
        }

        // 2. 校验时间
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getEnrollStartTime())) {
            return "报名还未开始！";
        }
        if (now.isAfter(activity.getEnrollEndTime())) {
            return "报名已经结束！";
        }

        // 3. 校验人数是否已满
        int enrolledCount = activityRecordMapper.countByActivityId(activityId);
        if (enrolledCount >= activity.getCapacity()) {
            return "手慢了，活动名额已满！";
        }

        // 4. 校验是否重复报名
        int isEnrolled = activityRecordMapper.checkEnrolled(activityId, studentId);
        if (isEnrolled > 0) {
            return "您已经报名过该活动，请勿重复报名！";
        }

        // 5. 一切校验通过，写入报名表！
        ActivityRecord record = new ActivityRecord();
        record.setActivityId(activityId);
        record.setStudentId(studentId);
        activityRecordMapper.insert(record);

        return "success"; // 代表报名成功
    }
    @Autowired
    private StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) // 开启大招：事务管理，一旦报错，所有修改自动回滚撤销！
    public String signActivity(String studentId, String activityId) {
        // 1. 检查有没有报名记录
        ActivityRecord record = activityRecordMapper.selectByActivityIdAndStudentId(activityId, studentId);
        if (record == null) {
            return "您尚未报名该活动，无法签到！";
        }

        // 2. 检查是否已经签到过（防止重复刷学分）
        if (record.getSignStatus() != null && record.getSignStatus() == 1) {
            return "您已经签到过了，请勿重复签到！";
        }

        // 3. 更新为已签到
        activityRecordMapper.updateSignStatus(record.getId());

        // 4. 查出这个活动能发多少学时，是什么类别
        Activity activity = activityMapper.selectById(activityId);
        if (activity != null && activity.getActivityHour() != null) {
            // 5. 自动把学时发到学生账户里
            studentMapper.addHour(studentId, activity.getHourType(), activity.getActivityHour());
        }

        return "success";
    }
    @Override
    public void auditActivity(String activityId, boolean isPass) {
        // 先查出活动
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        // 简化的状态机：通过则进入"待报名"让学生抢，驳回则变为"活动取消"或"已驳回"
        String newStatus = isPass ? "待报名" : "活动取消";

        activityMapper.updateStatus(activityId, newStatus);
    }
}