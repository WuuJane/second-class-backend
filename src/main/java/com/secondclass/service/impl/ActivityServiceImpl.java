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
}