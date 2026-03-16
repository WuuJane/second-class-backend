package com.secondclass.mapper;

import com.secondclass.entity.ActivityRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ActivityRecordMapper {
    // 插入一条报名记录
    int insert(ActivityRecord record);

    // 统计某个活动当前的已报名人数
    int countByActivityId(String activityId);

    // 检查这个学生是不是已经报过这个活动了 (查出来大于0就说明报过了)
    int checkEnrolled(@Param("activityId") String activityId, @Param("studentId") String studentId);
}