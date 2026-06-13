package com.secondclass.service.impl;

import com.secondclass.entity.CreditRequirement;
import com.secondclass.mapper.CreditRequirementMapper;
import com.secondclass.service.CreditRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditRequirementServiceImpl implements CreditRequirementService {

    @Autowired
    private CreditRequirementMapper requirementMapper;

    @Override
    public List<CreditRequirement> getAllRequirements() {
        return requirementMapper.selectAll();
    }

    @Override
    public void saveRequirement(CreditRequirement requirement) {
        if (requirement.getId() == null) {
            // 新增前双重条件校验
            CreditRequirement exist = requirementMapper.selectByCollegeAndGrade(requirement.getCollege(), requirement.getGrade());
            if (exist != null) {
                // 🔥 优化了报错信息，精确定位到哪个学院哪个年级
                throw new RuntimeException("【" + requirement.getCollege() + "】的【" + requirement.getGrade() + "】学时要求已存在，请直接在列表中点击编辑！");
            }
            requirementMapper.insert(requirement);
        } else {
            // 修改操作
            requirementMapper.update(requirement);
        }
    }

    @Override
    public void deleteRequirement(Long id) {
        requirementMapper.deleteById(id);
    }
}