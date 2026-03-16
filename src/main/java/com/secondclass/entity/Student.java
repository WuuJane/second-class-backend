package com.secondclass.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Student {
    private String studentId;           // 学号 (主键)
    private String studentName;         // 姓名
    private String studentDepartment;   // 所属院系
    private String grade;               // 年级 (如：2022级)
    private BigDecimal politicsHour;    // 政治理论素养学时
    private BigDecimal humanityHour;    // 人文素养与国际视野学时
    private BigDecimal innovationHour;  // 创新训练与创业实践学时
    private BigDecimal socialHour;      // 社会责任感与家国情怀学时
}