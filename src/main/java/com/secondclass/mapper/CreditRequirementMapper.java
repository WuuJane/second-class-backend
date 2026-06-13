package com.secondclass.mapper;

import com.secondclass.entity.CreditRequirement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CreditRequirementMapper {

    // 获取所有的配置列表
    List<CreditRequirement> selectAll();

    // 核心业务：根据学院和年级，精准匹配出对应的学时要求规则
    CreditRequirement selectByCollegeAndGrade(@Param("college") String college, @Param("grade") String grade);

    // 新增配置
    int insert(CreditRequirement requirement);

    // 修改配置
    int update(CreditRequirement requirement);

    // 删除配置
    int deleteById(Long id);
}