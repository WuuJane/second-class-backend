package com.secondclass.mapper;

import com.secondclass.entity.SysOrganization;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysOrganizationMapper {
    // 根据 ID 查询单条组织机构信息
    SysOrganization selectById(Integer orgId);
}