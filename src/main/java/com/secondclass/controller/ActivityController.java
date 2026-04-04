package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.dto.ActivityCreateDTO;
import com.secondclass.entity.Activity;
import com.secondclass.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/list")
    public ResultVO<List<Activity>> getActivityList() {
        List<Activity> list = activityService.getAllActivities();
        return ResultVO.success(list);
    }

    @PostMapping("/create")
    public ResultVO<Void> createActivity(@RequestBody ActivityCreateDTO dto) {
        activityService.createActivity(dto);
        return ResultVO.success();
    }

    @PostMapping("/enroll")
    public ResultVO<Void> enroll(@RequestParam String studentId, @RequestParam String activityId) {
        String result = activityService.enrollActivity(studentId, activityId);

        if ("success".equals(result)) {
            return ResultVO.success();
        } else {
            return ResultVO.error(result);
        }
    }

    @PostMapping("/sign")
    public ResultVO<Void> sign(@RequestParam String studentId, @RequestParam String activityId) {
        String result = activityService.signActivity(studentId, activityId);

        if ("success".equals(result)) {
            return ResultVO.success();
        } else {
            return ResultVO.error(result);
        }
    }

    @PostMapping("/audit")
    public ResultVO<Void> audit(@RequestParam String activityId, @RequestParam boolean isPass) {
        try {
            activityService.auditActivity(activityId, isPass);
            return ResultVO.success();
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 🌟 新增功能：负责人专用，查询我发布的活动
    @GetMapping("/my-manage")
    public ResultVO<List<Activity>> getMyManageActivities(@RequestParam String managerId) {
        List<Activity> list = activityService.getMyManageActivities(managerId);
        return ResultVO.success(list);
    }

    // 🌟 新增功能：负责人专用，查看报名名单（带学生详细信息）
    @GetMapping("/{activityId}/students")
    public ResultVO<List<Map<String, Object>>> getActivityEnrollList(@PathVariable String activityId) {
        List<Map<String, Object>> list = activityService.getActivityEnrollList(activityId);
        return ResultVO.success(list);
    }
}