package com.secondclass.mapper;

import com.secondclass.entity.CreditRequirement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CreditRequirementMapper {
    // 核心业务：根据学院和年级，精准匹配出对应的学时要求规则
    CreditRequirement selectByCollegeAndGrade(@Param("college") String college, @Param("grade") String grade);
}