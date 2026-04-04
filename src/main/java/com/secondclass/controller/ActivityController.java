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

    @GetMapping("/my-manage")
    public ResultVO<List<Activity>> getMyManageActivities(@RequestParam String managerId) {
        List<Activity> list = activityService.getMyManageActivities(managerId);
        return ResultVO.success(list);
    }

    @GetMapping("/{activityId}/students")
    public ResultVO<List<Map<String, Object>>> getActivityEnrollList(@PathVariable String activityId) {
        List<Map<String, Object>> list = activityService.getActivityEnrollList(activityId);
        return ResultVO.success(list);
    }

    //负责人撤销活动接口
    @PostMapping("/cancel")
    public ResultVO<Void> cancelActivity(@RequestParam String activityId, @RequestParam String managerId) {
        try {
            activityService.cancelActivity(activityId, managerId);
            return ResultVO.success();
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 负责人后台手动替学生签到
    // 逻辑和学生扫码一模一样，我们单独拉一个接口是为了前端权限区分
    @PostMapping("/manual-sign")
    public ResultVO<Void> manualSign(@RequestParam String studentId, @RequestParam String activityId) {
        String result = activityService.signActivity(studentId, activityId);
        if ("success".equals(result)) {
            return ResultVO.success(); // 签到成功且自动发学时了
        } else {
            return ResultVO.error(result);
        }
    }

    @PostMapping("/resubmit")
    public ResultVO<Void> resubmitActivity(@RequestBody Activity activity) {
        // 基本参数校验：修改必须带上活动 ID
        if (activity.getActivityId() == null) {
            return ResultVO.error("活动ID不能为空");
        }

        try {
            activityService.editAndResubmit(activity);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    @GetMapping("/{activityId}/attendance")
    public ResultVO<List<Map<String, Object>>> getActualAttendanceList(@PathVariable String activityId) {
        List<Map<String, Object>> list = activityService.getActualAttendanceList(activityId);
        return ResultVO.success(list);
    }

    /**
     * 负责人手动结束活动（发起完结审核）
     */
    @PostMapping("/finish")
    public ResultVO<Void> finishActivity(@RequestParam String activityId, @RequestParam String managerId) {
        try {
            activityService.finishActivity(activityId, managerId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }
}