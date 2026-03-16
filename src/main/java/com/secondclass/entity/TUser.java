package com.secondclass.entity;

import lombok.Data;

@Data
public class TUser {
    private String id;           // 工号 (主键)
    private String name;         // 姓名
    private String role;         // 角色：管理员 / 负责人 / 审核人
    private String department;   // 所属院系
    private Integer status;      // 状态：0-禁用，1-启用
}