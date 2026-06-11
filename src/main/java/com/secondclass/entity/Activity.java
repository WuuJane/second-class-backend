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
    private String activityStatus;   // 活动状态 (数据库存的人工审核/人工完结/撤销状态)
    private LocalDateTime createTime;// 创建时间
    private String activityImg;      // 活动海报
    private String location;         // 活动地点
    private Integer capacity;        // 人数限制
    private LocalDateTime enrollStartTime; // 报名开始时间
    private LocalDateTime enrollEndTime;   // 报名结束时间
    private Integer managerOrgId;    // 发起方的组织ID
    private Integer auditorOrgId;    // 动态绑定的审核组织ID

    /**
     * 🌟 核心修改：手动重写 getter 方法，实现基于时间的动态状态计算
     * Spring Boot 返回 JSON 给前端时，会自动调用这个方法，从而拿到最实时的状态
     */
    public String getActivityStatus() {
        // 1. 如果是特殊的人工业务状态，不受时间轴影响，直接返回数据库里存的值
        if ("等待初审".equals(this.activityStatus) ||
                "待终审".equals(this.activityStatus) ||
                "被驳回".equals(this.activityStatus) ||
                "已撤销".equals(this.activityStatus) ||
                "活动完结".equals(this.activityStatus)) {
            return this.activityStatus;
        }

        // 2. 如果属于正常发布的活动，根据当前系统时间 (LocalDateTime) 动态计算
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(this.enrollStartTime)) {
            return "未开始报名";
        } else if (now.isAfter(this.enrollStartTime) && now.isBefore(this.enrollEndTime)) {
            return "可报名";     // 💡 这里顺便帮你把原来的“待报名”改成了更直观的“可报名”
        } else if (now.isAfter(this.enrollEndTime) && now.isBefore(this.startTime)) {
            return "报名已结束";  // 处于报名截止到活动开始之间的尴尬期
        } else if (now.isAfter(this.startTime) && now.isBefore(this.endTime)) {
            return "活动进行中";
        } else {
            return "活动结束";
        }
    }
}