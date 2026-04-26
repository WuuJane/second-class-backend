package com.secondclass.service;

import com.secondclass.entity.SysOrganization;
import java.util.List;

public interface SysOrganizationService {

    // 1. 获取所有组织架构列表
    List<SysOrganization> getAllOrgs();

    // 2. 新增学院/组织
    void addOrg(SysOrganization org);

    // 3. 修改学院/组织信息
    void updateOrg(SysOrganization org);

    // 4. 删除组织
    void deleteOrg(String orgId);
}