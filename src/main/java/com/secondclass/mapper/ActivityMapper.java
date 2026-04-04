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
}