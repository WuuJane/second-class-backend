package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.dto.LoginDTO;
import com.secondclass.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResultVO<Object> login(@RequestBody LoginDTO loginDTO) {
        // 前端必须传 username, password 和 userType
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null || loginDTO.getUserType() == null) {
            return ResultVO.error("账号、密码或角色不能为空");
        }

        Object user = loginService.login(loginDTO);

        if (user != null) {
            return ResultVO.success(user);
        } else {
            return ResultVO.error("账号或密码错误");
        }
    }
}