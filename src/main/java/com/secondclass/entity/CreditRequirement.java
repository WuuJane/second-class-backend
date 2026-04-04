package com.secondclass.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreditRequirement {
    private Long id;                  // 主键ID (对应 bigint)
    private String college;           // 学院名称
    private String grade;             // 年级
    private BigDecimal politicsReq;   // 政治理论素养要求学时 (对应 decimal)
    private BigDecimal humanityReq;   // 人文素养与国际视野要求学时
    private BigDecimal innovationReq; // 创新训练与创业实践要求学时
    private BigDecimal socialReq;     // 社会责任感与家国情怀要求学时
    private LocalDateTime updateTime; // 最后修改时间 (对应 datetime)
}