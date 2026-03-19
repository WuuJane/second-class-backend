package com.secondclass.entity;

import lombok.Data;

@Data
public class Admin {

    private Long id;

    private String name;

    private String workNo; // 工号，对应数据库的 work_no

    private String password; // 登录密码
}