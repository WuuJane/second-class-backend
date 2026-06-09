package com.secondclass.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String username;     // 账号（学号/工号）
    private String oldPassword;  // 原密码
    private String newPassword;  // 新密码
    private Integer userType;    // 用户角色类型 (1-学生 2-教工 3-管理员)
}