package com.secondclass.mapper;

import com.secondclass.entity.TUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 根据工号查询用户（登录时用）
    TUser selectById(String id);
}