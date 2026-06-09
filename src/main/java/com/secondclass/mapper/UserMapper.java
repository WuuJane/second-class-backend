package com.secondclass.mapper;

import com.secondclass.entity.TUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    // 根据工号查询用户（登录时用）
    TUser selectById(String id);

    // 管理员功能：查询所有教职工
    List<TUser> selectAll();

    // 管理员功能：新增教职工
    int insert(TUser user);

    // 管理员功能：更新教职工信息
    int update(TUser user);

    // 管理员功能：删除教职工
    int deleteById(String id);

    // 管理员功能：启用/禁用账号
    int updateStatus(@Param("id") String id, @Param("status") Integer status);
}