package com.secondclass.service;

import com.secondclass.entity.TUser;
import com.secondclass.entity.Student;

public interface UserService {
    TUser getUserById(String id);
    Student getStudentById(String studentId);
}