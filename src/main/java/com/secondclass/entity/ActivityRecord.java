package com.secondclass.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityRecord {
    private Integer id;              // 主键自增
    private String activityId;       // 活动编号
    private String studentId;        // 学号
    private LocalDateTime enrollTime;// 报名时间
    private Integer signStatus;      // 签到状态：0-未签到，1-已签到
    private LocalDateTime signTime;  // 签到时间
}