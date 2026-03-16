package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.entity.Student;
import com.secondclass.entity.TUser;
import com.secondclass.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 查询教职工信息
    @GetMapping("/teacher/{id}")
    public ResultVO<TUser> getTeacher(@PathVariable String id) {
        TUser user = userService.getUserById(id);
        if (user == null) {
            return ResultVO.error("用户不存在");
        }
        return ResultVO.success(user);
    }

    // 查询学生信息
    @GetMapping("/student/{id}")
    public ResultVO<Student> getStudent(@PathVariable String id) {
        Student student = userService.getStudentById(id);
        if (student == null) {
            return ResultVO.error("学生不存在");
        }
        return ResultVO.success(student);
    }
}