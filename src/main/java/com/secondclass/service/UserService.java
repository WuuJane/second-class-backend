package com.secondclass.service;

import com.secondclass.entity.Admin;
import com.secondclass.entity.SysOrganization;
import com.secondclass.entity.TUser;
import com.secondclass.entity.Student;

import java.util.List;

public interface UserService {
    TUser getUserById(String id);
    Student getStudentById(String id);

    // ========== 教职工管理 ==========
    List<TUser> getAllUsers();
    void createUser(TUser user);
    void updateUser(TUser user);
    void deleteUser(String id);
    void toggleUserStatus(String id);

    // ========== 管理员账号管理 ==========
    List<Admin> getAllAdmins();
    void createAdmin(Admin admin);
    void updateAdmin(Admin admin);
    void deleteAdmin(Long id);

    // ========== 学生管理 ==========
    List<Student> getAllStudents();

    // ========== 组织机构 ==========
    List<SysOrganization> getAllOrganizations();
    void createOrganization(SysOrganization org);
    void updateOrganization(SysOrganization org);
    void deleteOrganization(Integer orgId);
    void createStudent(Student student);
    void updateStudent(Student student);
    void deleteStudent(String id);
}