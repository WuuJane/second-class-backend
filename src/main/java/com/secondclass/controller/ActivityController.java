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
}