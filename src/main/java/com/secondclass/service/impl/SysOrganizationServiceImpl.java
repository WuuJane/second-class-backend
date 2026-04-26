package com.secondclass.service.impl;

import com.secondclass.entity.SysOrganization;
import com.secondclass.mapper.SysOrganizationMapper;
import com.secondclass.mapper.UserMapper;
import com.secondclass.service.SysOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service // 告诉Spring这是一个Service服务类
public class SysOrganizationServiceImpl implements SysOrganizationService {

    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;

    @Autowired
    private UserMapper userMapper; // 引入UserMapper是为了后面做安全校验

    @Override
    public List<SysOrganization> getAllOrgs() {
        return sysOrganizationMapper.selectAllOrgs();
    }

    @Override
    public void addOrg(SysOrganization org) {
        // 1. 业务校验：防止插入两个名字一模一样的学院
        SysOrganization existOrg = sysOrganizationMapper.selectByName(org.getOrgName());
        if (existOrg != null) {
            throw new RuntimeException("该组织名称已存在！");
        }

        // 2. 生成一个随机的32位UUID作为主键
        org.setOrgId(UUID.randomUUID().toString().replace("-", ""));

        // 3. 存入数据库
        sysOrganizationMapper.insertOrg(org);
    }

    @Override
    public void updateOrg(SysOrganization org) {
        // 直接调用更新语句
        sysOrganizationMapper.updateOrg(org);
    }

    @Override
    public void deleteOrg(String orgId) {
        // 🚨 核心防御逻辑：
        // 管理员想删掉一个学院，必须先检查这个学院里是不是还有负责人或审核人的账号。
        // 如果连锅端了，那些账号就成了“孤魂野鬼”，系统会报错。
        int userCount = userMapper.countUsersByOrgId(orgId);
        if (userCount > 0) {
            // 如果查出来这个学院还有人，直接打断删除，抛出报错信息给前端
            throw new RuntimeException("删除失败：该组织下还有绑定的用户账号，请先转移或删除账号！");
        }

        // 如果没人在这个学院了，就可以安全删除了
        sysOrganizationMapper.deleteById(orgId);
    }
}