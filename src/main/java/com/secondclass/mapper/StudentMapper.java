package com.secondclass.mapper;

import com.secondclass.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface StudentMapper {
    // 根据学号查询学生
    Student selectById(String studentId);

    // 给学生发放对应类别的学时
    int addHour(@Param("studentId") String studentId, @Param("hourType") String hourType, @Param("hour") BigDecimal hour);

    // 管理员功能：查询所有学生
    List<Student> selectAll();

    // 管理员功能：启用/禁用学生账号
    int updateStatus(@Param("studentId") String studentId, @Param("status") Integer status);
    int insert(Student student);

    /**
     * 更新学生数据
     */
    int update(Student student);

    /**
     * 根据学号/ID删除学生
     */
    int deleteById(String id);

    // 管理员功能：重置密码
    int updatePassword(@Param("studentId") String studentId, @Param("password") String password);
}