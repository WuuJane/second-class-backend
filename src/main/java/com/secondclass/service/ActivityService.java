package com.secondclass.service;

import com.secondclass.dto.ActivityCreateDTO;
import com.secondclass.dto.ImportResultDTO;
import com.secondclass.entity.Activity;
import org.springframework.web.multipart.MultipartFile;

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
    String signActivity(String studentId, String activityId, String signCode);

    // 审核活动（isPass: true代表通过，false代表驳回，rejectReason为驳回原因，auditorRole用于校验审核人与阶段的匹配）
    void auditActivity(String activityId, boolean isPass, String rejectReason, String auditorRole);

    // 获取负责人自己发布的活动
    List<Activity> getMyManageActivities(String managerId);

    // 获取某个活动的报名名单明细
    List<Map<String, Object>> getActivityEnrollList(String activityId);

    //负责人撤销活动
    void cancelActivity(String activityId, String managerId);

    void editAndResubmit(Activity activity);
    List<Map<String, Object>> getActualAttendanceList(String activityId);
    void finishActivity(String activityId, String managerId);

    // 根据活动ID获取活动详情
    Activity getActivityById(String activityId);

    //获取学生已报名的活动列表
    List<Activity> getMyEnrolledActivities(String studentId);

    //学生取消报名
    void cancelEnroll(String studentId, String activityId);

    // 获取学生已签到的历史活动记录
    List<Activity> getHistoryActivities(String studentId);
    void managerAddStudent(String studentId, String activityId);
    void manualSign(String studentId, String activityId);
    void cancelSign(String studentId, String activityId);

    // Excel 导入学生名单
    ImportResultDTO importStudentsFromExcel(MultipartFile file, String activityId);
}