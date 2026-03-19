package com.secondclass.dto;

import lombok.Data;

@Data
public class LoginDTO {

    /**
     * 前端传来的账号（学生的学号，或者老师/管理员的工号）
     */
    private String username;

    /**
     * 前端传来的密码
     */
    private String password;

    /**
     * 用户类型：
     * 1 代表学生
     * 2 代表审核老师/负责人 (t_user)
     * 3 代表系统管理员 (admin)
     */
    private Integer userType;
}