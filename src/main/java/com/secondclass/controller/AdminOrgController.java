package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.entity.SysOrganization;
import com.secondclass.service.SysOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/org")
public class AdminOrgController {

    @Autowired
    private SysOrganizationService sysOrganizationService;

    /**
     * 1. 获取所有组织架构列表
     */
    @GetMapping("/list")
    public ResultVO<List<SysOrganization>> getOrgList() {
        List<SysOrganization> list = sysOrganizationService.getAllOrgs();
        return ResultVO.success(list);
    }

    /**
     * 2. 新增学院/组织
     */
    @PostMapping("/add")
    public ResultVO<Void> addOrg(@RequestBody SysOrganization org) {
        try {
            sysOrganizationService.addOrg(org);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 3. 修改学院/组织信息（比如修改它的上级审核单位）
     */
    @PutMapping("/update")
    public ResultVO<Void> updateOrg(@RequestBody SysOrganization org) {
        if (org.getOrgId() == null) {
            return ResultVO.error("组织ID不能为空");
        }
        sysOrganizationService.updateOrg(org);
        return ResultVO.success();
    }

    /**
     * 4. 删除组织（危险操作，需校验该组织下是否还有人）
     */
    @DeleteMapping("/delete/{orgId}")
    public ResultVO<Void> deleteOrg(@PathVariable String orgId) {
        try {
            sysOrganizationService.deleteOrg(orgId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }
}