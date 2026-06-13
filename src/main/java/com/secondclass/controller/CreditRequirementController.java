package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.entity.CreditRequirement;
import com.secondclass.service.CreditRequirementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/credit-requirement")
public class CreditRequirementController {

    @Autowired
    private CreditRequirementService requirementService;

    // 获取所有配置列表
    @GetMapping("/list")
    public ResultVO<List<CreditRequirement>> list() {
        return ResultVO.success(requirementService.getAllRequirements());
    }

    // 新增或修改配置 (保存)
    @PostMapping("/save")
    public ResultVO<Void> save(@RequestBody CreditRequirement requirement) {
        try {
            requirementService.saveRequirement(requirement);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 删除配置
    @DeleteMapping("/delete/{id}")
    public ResultVO<Void> delete(@PathVariable Long id) {
        requirementService.deleteRequirement(id);
        return ResultVO.success();
    }
}