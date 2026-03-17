package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.dto.ActivityCreateDTO;
import com.secondclass.entity.Activity;
import com.secondclass.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 新增的创建活动接口
    @PostMapping("/create")
    public ResultVO<Void> createActivity(@RequestBody ActivityCreateDTO dto) {
        activityService.createActivity(dto);
        return ResultVO.success(); // 返回成功，不需要返回具体数据
    }
    // 学生报名活动
    @PostMapping("/enroll")
    public ResultVO<Void> enroll(@RequestParam String studentId, @RequestParam String activityId) {
        String result = activityService.enrollActivity(studentId, activityId);

        if ("success".equals(result)) {
            return ResultVO.success(); // 报成了
        } else {
            return ResultVO.error(result); // 没报成，把 Service 里的错误原因返回给前端
        }
    }
    // 学生扫描签到码进行签到
    @PostMapping("/sign")
    public ResultVO<Void> sign(@RequestParam String studentId, @RequestParam String activityId) {
        String result = activityService.signActivity(studentId, activityId);

        if ("success".equals(result)) {
            return ResultVO.success(); // 签到成功并已发放学时
        } else {
            return ResultVO.error(result); // 返回拦截原因（比如：没报名、已签到过）
        }
    }
    // 管理员审核活动
    @PostMapping("/audit")
    public ResultVO<Void> audit(@RequestParam String activityId, @RequestParam boolean isPass) {
        try {
            activityService.auditActivity(activityId, isPass);
            return ResultVO.success();
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }
}