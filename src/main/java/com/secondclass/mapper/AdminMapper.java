package com.secondclass.mapper;

import com.secondclass.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {

    /**
     * 根据工号查询管理员信息（登录时使用）
     */
    Admin selectByWorkNo(String workNo);

    // 管理员功能：查询所有管理员
    List<Admin> selectAll();

    // 管理员功能：新增管理员
    int insert(Admin admin);

    // 管理员功能：更新管理员信息
    int update(Admin admin);

    // 管理员功能：删除管理员
    int deleteById(Long id);
}