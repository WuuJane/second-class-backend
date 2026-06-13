package com.secondclass.service.impl;

import com.secondclass.entity.SysOrganization;
import com.secondclass.mapper.SysOrganizationMapper;
import com.secondclass.mapper.UserMapper;
import com.secondclass.service.SysOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 告诉Spring这是一个Service服务类
public class SysOrganizationServiceImpl implements SysOrganizationService {

    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;

    @Autowired
    private UserMapper userMapper; // 引入UserMapper是为了后面做安全校验

    @Override
    public List<SysOrganization> getAllOrgs() {
        return sysOrganizationMapper.selectAll();
    }

    @Override
    public void addOrg(SysOrganization org) {
        // 1. 业务校验：防止插入两个名字一模一样的学院
        List<SysOrganization> all = sysOrganizationMapper.selectAll();
        for (SysOrganization o : all) {
            if (o.getOrgName() != null && o.getOrgName().equals(org.getOrgName())) {
                throw new RuntimeException("该组织名称已存在！");
            }
        }

        // 3. 存入数据库；orgId 使用数据库自增或由 Mapper 处理
        sysOrganizationMapper.insert(org);
    }

    @Override
    public void updateOrg(SysOrganization org) {
        // 直接调用更新语句
        sysOrganizationMapper.update(org);
    }

    @Override
    public void deleteOrg(String orgId) {
        // 🚨 核心防御逻辑：
        // 管理员想删掉一个学院，必须先检查这个学院里是不是还有负责人或审核人的账号。
        // 如果连锅端了，那些账号就成了“孤魂野鬼”，系统会报错。
        // count users by comparing the Integer orgId field
        int userCount = 0;
        try {
            Integer id = Integer.valueOf(orgId);
            List<com.secondclass.entity.TUser> users = userMapper.selectAll();
            for (com.secondclass.entity.TUser u : users) {
                if (u.getOrgId() != null && u.getOrgId().equals(id)) {
                    userCount++;
                }
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("无效的组织ID");
        }
        if (userCount > 0) {
            // 如果查出来这个学院还有人，直接打断删除，抛出报错信息给前端
            throw new RuntimeException("删除失败：该组织下还有绑定的用户账号，请先转移或删除账号！");
        }

        // 如果没人在这个学院了，就可以安全删除了
        sysOrganizationMapper.deleteById(Integer.valueOf(orgId));
    }
}