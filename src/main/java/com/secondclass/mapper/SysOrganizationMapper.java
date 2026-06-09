package com.secondclass.mapper;

import com.secondclass.entity.SysOrganization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysOrganizationMapper {
    // 根据 ID 查询单条组织机构信息
    SysOrganization selectById(Integer orgId);

    // 管理员功能：查询所有组织机构（用于创建用户时选择归属组织）
    List<SysOrganization> selectAll();

    // 管理员功能：新增组织机构
    int insert(SysOrganization org);

    // 管理员功能：更新组织机构
    int update(SysOrganization org);

    // 管理员功能：删除组织机构
    int deleteById(Integer orgId);
}