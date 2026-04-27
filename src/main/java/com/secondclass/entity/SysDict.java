package com.secondclass.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysDict {
    private Integer id;           // 字典主键（自增即可）
    private String dictType;      // 字典类型（重要！比如：activity_status, sign_type）
    private String dictLabel;     // 字典标签（展示给用户看的，比如：待报名、扫一扫）
    private Integer dictValue;    // 字典键值（存入数据库的真实值，比如：1, 2, 3）
    private Integer sortOrder;    // 排序（决定下拉框里谁在前面）
    private Integer status;       // 状态：1-正常，0-停用（不删，只停用）
    private LocalDateTime createTime;
}