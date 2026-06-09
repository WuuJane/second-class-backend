package com.secondclass.service;

import com.secondclass.dto.ChangePasswordDTO;
import com.secondclass.dto.LoginDTO;
import com.secondclass.entity.Admin;
import com.secondclass.entity.Student;
import com.secondclass.entity.TUser;
import com.secondclass.mapper.AdminMapper;
import com.secondclass.mapper.StudentMapper;
import com.secondclass.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils; // 🔥 引入 Spring 自带的加密工具

@Service
public class LoginService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 🔥 MD5 加密工具方法
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    /**
     * 🔥 密码比对校验（双向兼容）
     * 既兼容数据库里老的明文 123456，也支持校验新修改的 MD5 密文
     */
    private boolean checkPassword(String inputPassword, String dbPassword) {
        if (dbPassword == null) return false;
        // 1. 如果数据库里的密码和输入的明文直接相等，说明是老账号没改过密码，放行
        if (dbPassword.equals(inputPassword)) return true;
        // 2. 如果数据库里的密码和输入密码的 MD5 加密值相等，说明是改过密码的，放行
        if (dbPassword.equals(encryptPassword(inputPassword))) return true;

        return false;
    }

    public Object login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        Integer userType = loginDTO.getUserType();

        if (userType == 1) {
            Student student = studentMapper.selectById(username);
            // 💡 替换原来的 equals 校验为我们封装的 checkPassword
            if (student != null && checkPassword(password, student.getPassword())) {
                student.setPassword(null);
                return student;
            }
        } else if (userType == 2) {
            TUser tUser = userMapper.selectById(username);
            if (tUser != null && checkPassword(password, tUser.getPassword())) {
                tUser.setPassword(null);
                return tUser;
            }
        } else if (userType == 3) {
            Admin admin = adminMapper.selectByWorkNo(username);
            if (admin != null && checkPassword(password, admin.getPassword())) {
                admin.setPassword(null);
                return admin;
            }
        }

        return null;
    }

    /**
     * 🔥 新增：修改密码逻辑
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordDTO dto) {
        String username = dto.getUsername();
        String oldPassword = dto.getOldPassword();
        // 直接将新密码加密为 MD5 准备存入数据库
        String newPasswordEncrypted = encryptPassword(dto.getNewPassword());
        Integer userType = dto.getUserType();

        if (userType == 1) {
            Student student = studentMapper.selectById(username);
            if (student == null || !checkPassword(oldPassword, student.getPassword())) {
                throw new RuntimeException("原密码错误！");
            }
            student.setPassword(newPasswordEncrypted);
            studentMapper.update(student);
        } else if (userType == 2) {
            TUser tUser = userMapper.selectById(username);
            if (tUser == null || !checkPassword(oldPassword, tUser.getPassword())) {
                throw new RuntimeException("原密码错误！");
            }
            tUser.setPassword(newPasswordEncrypted);
            userMapper.update(tUser);
        } else if (userType == 3) {
            Admin admin = adminMapper.selectByWorkNo(username);
            if (admin == null || !checkPassword(oldPassword, admin.getPassword())) {
                throw new RuntimeException("原密码错误！");
            }
            admin.setPassword(newPasswordEncrypted);
            adminMapper.update(admin);
        } else {
            throw new RuntimeException("非法的用户类型");
        }
    }
}