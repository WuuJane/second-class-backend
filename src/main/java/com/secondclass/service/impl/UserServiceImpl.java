package com.secondclass.service.impl;

import com.secondclass.entity.Student;
import com.secondclass.entity.TUser;
import com.secondclass.mapper.StudentMapper;
import com.secondclass.mapper.UserMapper;
import com.secondclass.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public TUser getUserById(String id) {
        return userMapper.selectById(id);
    }

    @Override
    public Student getStudentById(String studentId) {
        return studentMapper.selectById(studentId);
    }
}