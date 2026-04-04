package com.secondclass.service;

import com.secondclass.dto.ActivityCreateDTO;
import com.secondclass.entity.Activity;
import java.util.List;
import java.util.Map;

public interface ActivityService {

    // 原有的：查询所有活动
    List<Activity> getAllActivities();

    // 新增的：创建活动
    void createActivity(ActivityCreateDTO dto);

    // 学生报名活动，返回提示信息
    String enrollActivity(String studentId, String activityId);

    // 学生签到并结算学时
    String signActivity(String studentId, String activityId);

    // 审核活动（isPass: true代表通过，false代表驳回）
    void auditActivity(String activityId, boolean isPass);

    // 获取负责人自己发布的活动
    List<Activity> getMyManageActivities(String managerId);

    // 获取某个活动的报名名单明细
    List<Map<String, Object>> getActivityEnrollList(String activityId);

    //负责人撤销活动
    void cancelActivity(String activityId, String managerId);
}