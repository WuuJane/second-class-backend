package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.dto.ActivityCreateDTO;
import com.secondclass.dto.ImportResultDTO;
import com.secondclass.entity.Activity;
import com.secondclass.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // 发起活动
    @PostMapping("/create")
    public ResultVO<Void> createActivity(@RequestBody ActivityCreateDTO dto) {
        try {
            activityService.createActivity(dto);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 学生报名活动接口
    @PostMapping("/enroll")
    public ResultVO<Void> enroll(@RequestParam String studentId, @RequestParam String activityId) {
        try {
            activityService.enrollActivity(studentId, activityId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 审核活动
    @PostMapping("/audit")
    public ResultVO<Void> audit(@RequestParam String activityId, @RequestParam boolean isPass,
                                @RequestParam(required = false) String rejectReason) {
        try {
            activityService.auditActivity(activityId, isPass, rejectReason);
            return ResultVO.success();
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 获取我管理的活动
    @GetMapping("/my-manage")
    public ResultVO<List<Activity>> getMyManageActivities(@RequestParam String managerId) {
        List<Activity> list = activityService.getMyManageActivities(managerId);
        return ResultVO.success(list);
    }

    // 获取活动名单
    @GetMapping("/{activityId}/students")
    public ResultVO<List<Map<String, Object>>> getActivityEnrollList(@PathVariable String activityId) {
        List<Map<String, Object>> list = activityService.getActivityEnrollList(activityId);
        return ResultVO.success(list);
    }

    // 负责人撤销活动接口
    @PostMapping("/cancel")
    public ResultVO<Void> cancelActivity(@RequestParam String activityId, @RequestParam String managerId) {
        try {
            activityService.cancelActivity(activityId, managerId);
            return ResultVO.success();
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 修改重提
    @PostMapping("/resubmit")
    public ResultVO<Void> resubmitActivity(@RequestBody Activity activity) {
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

    // 获取考勤名单
    @GetMapping("/{activityId}/attendance")
    public ResultVO<List<Map<String, Object>>> getActualAttendanceList(@PathVariable String activityId) {
        List<Map<String, Object>> list = activityService.getActualAttendanceList(activityId);
        return ResultVO.success(list);
    }

    // 负责人手动结束活动（发起完结审核）
    @PostMapping("/finish")
    public ResultVO<Void> finishActivity(@RequestParam String activityId, @RequestParam String managerId) {
        try {
            activityService.finishActivity(activityId, managerId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // ================== 签到与特权管理模块 ==================

    // 1. 学生自己扫码/输入签到码签到 (保留这唯一的一个 /sign 接口)
    @PostMapping("/sign")
    public ResultVO<Void> signActivity(
            @RequestParam String studentId,
            @RequestParam String activityId,
            @RequestParam(required = false) String signCode) {
        // 返回 success 字符串代表成功，否则返回错误信息
        String result = activityService.signActivity(studentId, activityId, signCode);
        if ("success".equals(result)) {
            return ResultVO.success();
        } else {
            return ResultVO.error(result);
        }
    }

    // 2. 负责人特权：直接添加学生入名单
    @PostMapping("/manager-add")
    public ResultVO<Void> managerAddStudent(@RequestParam String studentId, @RequestParam String activityId) {
        try {
            activityService.managerAddStudent(studentId, activityId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 3. 负责人特权：手动替学生补签
    @PostMapping("/manual-sign")
    public ResultVO<Void> manualSign(@RequestParam String studentId, @RequestParam String activityId) {
        try {
            activityService.manualSign(studentId, activityId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 4. 负责人特权：撤销学生的签到
    @PostMapping("/cancel-sign")
    public ResultVO<Void> cancelSign(@RequestParam String studentId, @RequestParam String activityId) {
        try {
            activityService.cancelSign(studentId, activityId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // ================== 学生前端门户模块 ==================

    // 获取活动大厅所有活动列表
    @GetMapping("/list")
    public ResultVO<List<Activity>> getActivityList() {
        List<Activity> list = activityService.getAllActivities();
        return ResultVO.success(list);
    }

    // 获取活动详情
    @GetMapping("/detail/{activityId}")
    public ResultVO<Activity> getActivityDetail(@PathVariable String activityId) {
        Activity activity = activityService.getActivityById(activityId);
        return ResultVO.success(activity);
    }

    // 获取我报名的活动列表
    @GetMapping("/my-enroll")
    public ResultVO<List<Activity>> getMyEnrolledActivities(@RequestParam String studentId) {
        List<Activity> list = activityService.getMyEnrolledActivities(studentId);
        return ResultVO.success(list);
    }

    // 学生取消报名
    @PostMapping("/cancel-enroll")
    public ResultVO<Void> cancelEnroll(@RequestParam String studentId, @RequestParam String activityId) {
        try {
            activityService.cancelEnroll(studentId, activityId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // 获取已获学时的历史活动
    @GetMapping("/history")
    public ResultVO<List<Activity>> getHistoryActivities(@RequestParam String studentId) {
        List<Activity> list = activityService.getHistoryActivities(studentId);
        return ResultVO.success(list);
    }

    // Excel 批量导入学生名单
    @PostMapping("/import-students")
    public ResultVO<ImportResultDTO> importStudents(@RequestParam MultipartFile file,
                                                     @RequestParam String activityId) {
        if (file.isEmpty()) {
            return ResultVO.error("请上传 Excel 文件");
        }
        ImportResultDTO result = activityService.importStudentsFromExcel(file, activityId);
        return ResultVO.success(result);
    }
}