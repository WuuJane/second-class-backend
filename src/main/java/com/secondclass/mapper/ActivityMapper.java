package com.secondclass.mapper;

import com.secondclass.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ActivityMapper {
    // 之前写的查询
    List<Activity> selectAll();
    // 根据 ID 查询单条活动
    Activity selectById(String activityId);
    int insert(Activity activity);
    // 更新活动状态
    int updateStatus(@Param("activityId") String activityId, @Param("status") String status);

    // 🌟 新增功能：查询某个负责人发布的所有活动
    List<Activity> selectByManagerId(String managerId);

    int updateActivity(Activity activity);
    // 自动更新为进行中（活动时间已到）
    int updateStatusToInProgress();

    //获取活动详情
    Activity getActivityById(String activityId);

    //查询学生已报名的活动列表
    List<Activity> getMyEnrolledActivities(String studentId);

    //取消报名
    int deleteEnrollRecord(@Param("studentId") String studentId, @Param("activityId") String activityId);

    //获取学生已签到的历史活动记录
    List<Activity> getHistoryActivities(String studentId);

    //悲观锁查询：查询活动信息并锁定该行数据，防止并发修改
    Activity selectByIdForUpdate(String activityId);
}
