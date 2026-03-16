package com.secondclass.mapper;

import com.secondclass.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper {
    // 根据学号查询学生（登录时用）
    Student selectById(String studentId);
}