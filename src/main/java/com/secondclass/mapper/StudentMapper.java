package com.secondclass.mapper;

import com.secondclass.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;

@Mapper
public interface StudentMapper {
    // 根据学号查询学生
    Student selectById(String studentId);

    // 给学生发放对应类别的学时
    int addHour(@Param("studentId") String studentId, @Param("hourType") String hourType, @Param("hour") BigDecimal hour);
}