package com.secondclass.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysOrganization {
    private Integer orgId;
    private String orgName;
    private String orgLevel;
    private String collegeName;
    private String orgType;
    private Integer auditorOrgId;
    private LocalDateTime createTime;
}