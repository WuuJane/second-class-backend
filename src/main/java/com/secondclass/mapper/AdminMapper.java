package com.secondclass.mapper;

import com.secondclass.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

    /**
     * 根据工号查询管理员信息（登录时使用）
     * 注意：这里的 workNo 对应数据库里的 work_no 字段
     */
    Admin selectByWorkNo(String workNo);

}