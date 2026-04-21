package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.entity.Student;
import com.secondclass.service.UserService; // 假设查询学生的逻辑在 UserService 里
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private UserService userService;

    /**
     * 获取学生个人信息与学时进度 (我的学时模块)
     */
    @GetMapping("/info")
    public ResultVO<Student> getStudentInfo(@RequestParam String studentId) {
        // 调用 Service 层去数据库查这个学生
        Student student = userService.getStudentById(studentId);
        if (student != null) {
            // 为了安全，不要把密码传给前端
            student.setPassword(null);
            return ResultVO.success(student);
        } else {
            return ResultVO.error("未找到该学生信息");
        }
    }
}