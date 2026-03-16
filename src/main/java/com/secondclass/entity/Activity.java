package com.secondclass.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动实体类
 */
@Data
public class Activity {
    private String activityId;       // 活动编号 (主键)
    private String activityName;     // 活动名称
    private String department;       // 所属院系
    private String hostUnit;         // 主办方名称
    private String managerId;        // 负责人工号
    private String auditorId;        // 审核人工号
    private BigDecimal activityHour; // 活动学时
    private String hourType;         // 学时类别
    private LocalDateTime startTime; // 活动开始时间
    private LocalDateTime endTime;   // 活动结束时间
    private String signType;         // 签到方式
    private String activityStatus;   // 活动状态
    private LocalDateTime createTime;// 创建时间

    // 后来补充的字段
    private String activityImg;      // 活动海报
    private String location;         // 活动地点
    private Integer capacity;        // 人数限制
    private LocalDateTime enrollStartTime; // 报名开始时间
    private LocalDateTime enrollEndTime;   // 报名结束时间
}