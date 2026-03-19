package com.secondclass.service;

import com.secondclass.dto.LoginDTO;
import com.secondclass.entity.Admin;
import com.secondclass.entity.Student;
import com.secondclass.entity.TUser;
import com.secondclass.mapper.AdminMapper;
import com.secondclass.mapper.StudentMapper;
import com.secondclass.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 统一登录逻辑
     */
    public Object login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        Integer userType = loginDTO.getUserType();

        // 1. 如果是学生登录 (userType == 1)
        if (userType == 1) {
            // 调用你写好的 StudentMapper
            Student student = studentMapper.selectById(username);
            // 校验账号是否存在，且密码是否正确
            if (student != null && password.equals(student.getPassword())) {
                student.setPassword(null); // 登录成功后，把密码清空再返回给前端，保护安全
                return student;
            }
        }
        // 2. 如果是教师/审核员/负责人登录 (userType == 2)
        else if (userType == 2) {
            // 调用你写好的 UserMapper
            TUser tUser = userMapper.selectById(username);
            if (tUser != null && password.equals(tUser.getPassword())) {
                tUser.setPassword(null);
                return tUser;
            }
        }
        // 3. 如果是管理员登录 (userType == 3)
        else if (userType == 3) {
            // 调用我们刚刚新建的 AdminMapper
            Admin admin = adminMapper.selectByWorkNo(username);
            if (admin != null && password.equals(admin.getPassword())) {
                admin.setPassword(null);
                return admin;
            }
        }

        // 如果代码走到这里，说明上面三个 if 都没有成功匹配（账号不存在或密码错误）
        return null;
    }
}