package com.secondclass.service;

import com.secondclass.dto.ActivityCreateDTO; // 🌟 别忘了这行导包！
import com.secondclass.entity.Activity;
import java.util.List;

public interface ActivityService {

    // 原有的：查询所有活动
    List<Activity> getAllActivities();

    // 新增的：创建活动
    void createActivity(ActivityCreateDTO dto);

    // 学生报名活动，返回提示信息
    String enrollActivity(String studentId, String activityId);
}