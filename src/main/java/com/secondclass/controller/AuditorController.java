package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.entity.Activity;
import com.secondclass.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/auditor")
public class AuditorController {

    @Autowired
    private ActivityService activityService;

    // 获取待审批列表（初审/终审）
    @GetMapping("/audit-list")
    public ResultVO<List<Activity>> getAuditList(@RequestParam String orgId) {
        List<Activity> all = activityService.getAllActivities();
        List<Activity> filtered = new ArrayList<>();
        for (Activity a : all) {
            String auditorOrg = a.getAuditorOrgId() == null ? null : String.valueOf(a.getAuditorOrgId());
            if (orgId.equals(auditorOrg)) {
                String status = a.getActivityStatus();
                if ("等待初审".equals(status) || "待终审".equals(status)) {
                    filtered.add(a);
                }
            }
        }
        return ResultVO.success(filtered);
    }

    // 获取待结算/完结审核列表（活动已结束等待最终审核）
    @GetMapping("/settle-list")
    public ResultVO<List<Activity>> getSettleList(@RequestParam String orgId) {
        List<Activity> all = activityService.getAllActivities();
        List<Activity> filtered = new ArrayList<>();
        for (Activity a : all) {
            String auditorOrg = a.getAuditorOrgId() == null ? null : String.valueOf(a.getAuditorOrgId());
            if (orgId.equals(auditorOrg) && "活动结束".equals(a.getActivityStatus())) {
                filtered.add(a);
            }
        }
        return ResultVO.success(filtered);
    }

    // 审批接口（通过）
    @PostMapping("/approve")
    public ResultVO<Void> approve(@RequestParam String activityId) {
        // 使用 ActivityService 的布尔型审核方法：true 表示通过
        activityService.auditActivity(activityId, true);
        return ResultVO.success();
    }

    // 驳回接口
    @PostMapping("/reject")
    public ResultVO<Void> reject(@RequestParam String activityId) {
        activityService.auditActivity(activityId, false);
        return ResultVO.success();
    }

    // 结算/完结审核接口（最终完结，由审核人确认活动结束并发学时）
    @PostMapping("/settle")
    public ResultVO<Void> settle(@RequestParam String activityId) {
        try {
            // reuse auditActivity(true) to mark final approval of an "活动结束" -> "活动完结"
            activityService.auditActivity(activityId, true);
            return ResultVO.success();
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }
}