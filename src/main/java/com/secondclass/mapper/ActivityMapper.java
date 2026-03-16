package com.secondclass.mapper;

import com.secondclass.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ActivityMapper {
    // 之前写的查询
    List<Activity> selectAll();
    // 根据 ID 查询单条活动
    Activity selectById(String activityId);
    int insert(Activity activity);
}