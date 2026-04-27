package com.secondclass.mapper;

import com.secondclass.entity.SysDict;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysDictMapper {
    List<SysDict> selectByType(String dictType);
    SysDict checkValueExist(@Param("dictType") String dictType, @Param("dictValue") Integer dictValue);
    int insertDict(SysDict sysDict);
    int updateDict(SysDict sysDict);
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}