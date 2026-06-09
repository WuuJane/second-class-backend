package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.entity.Admin;
import com.secondclass.entity.Student;
import com.secondclass.entity.SysOrganization;
import com.secondclass.entity.TUser;
import com.secondclass.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ==================== 教职工管理 ====================

    /**
     * 获取所有教职工列表
     */
    @GetMapping("/teacher/list")
    public ResultVO<List<TUser>> getTeacherList() {
        List<TUser> list = userService.getAllUsers();
        // 脱敏：清除密码
        list.forEach(u -> u.setPassword(null));
        return ResultVO.success(list);
    }

    /**
     * 根据工号查询教职工
     */
    @GetMapping("/teacher/{id}")
    public ResultVO<TUser> getTeacher(@PathVariable String id) {
        TUser user = userService.getUserById(id);
        if (user == null) {
            return ResultVO.error("用户不存在");
        }
        user.setPassword(null);
        return ResultVO.success(user);
    }

    /**
     * 新增教职工
     */
    @PostMapping("/teacher/create")
    public ResultVO<Void> createTeacher(@RequestBody TUser user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            return ResultVO.error("工号不能为空");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            return ResultVO.error("姓名不能为空");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            return ResultVO.error("角色不能为空");
        }
        try {
            userService.createUser(user);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 更新教职工信息
     */
    @PutMapping("/teacher/update")
    public ResultVO<Void> updateTeacher(@RequestBody TUser user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            return ResultVO.error("工号不能为空");
        }
        try {
            userService.updateUser(user);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 删除教职工
     */
    @DeleteMapping("/teacher/{id}")
    public ResultVO<Void> deleteTeacher(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 启用/禁用教职工账号
     */
    @PutMapping("/teacher/status/{id}")
    public ResultVO<Void> toggleTeacherStatus(@PathVariable String id) {
        try {
            userService.toggleUserStatus(id);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // ==================== 管理员账号管理 ====================

    /**
     * 获取所有系统管理员列表
     */
    @GetMapping("/admin/list")
    public ResultVO<List<Admin>> getAdminList() {
        List<Admin> list = userService.getAllAdmins();
        list.forEach(a -> a.setPassword(null));
        return ResultVO.success(list);
    }

    /**
     * 新增系统管理员
     */
    @PostMapping("/admin/create")
    public ResultVO<Void> createAdmin(@RequestBody Admin admin) {
        if (admin.getWorkNo() == null || admin.getWorkNo().isEmpty()) {
            return ResultVO.error("工号不能为空");
        }
        if (admin.getName() == null || admin.getName().isEmpty()) {
            return ResultVO.error("姓名不能为空");
        }
        try {
            userService.createAdmin(admin);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 更新管理员信息
     */
    @PutMapping("/admin/update")
    public ResultVO<Void> updateAdmin(@RequestBody Admin admin) {
        try {
            userService.updateAdmin(admin);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 删除系统管理员
     */
    @DeleteMapping("/admin/{id}")
    public ResultVO<Void> deleteAdmin(@PathVariable Long id) {
        try {
            userService.deleteAdmin(id);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    // ==================== 学生管理 ====================

    /**
     * 获取所有学生列表
     */
    @GetMapping("/student/list")
    public ResultVO<List<Student>> getStudentList() {
        List<Student> list = userService.getAllStudents();
        list.forEach(s -> s.setPassword(null));
        return ResultVO.success(list);
    }

    /**
     * 根据学号查询学生
     */
    @GetMapping("/student/{id}")
    public ResultVO<Student> getStudent(@PathVariable String id) {
        Student student = userService.getStudentById(id);
        if (student == null) {
            return ResultVO.error("学生不存在");
        }
        student.setPassword(null);
        return ResultVO.success(student);
    }
    /**
     * 新增学生账号 (新增补全)
     */
    @PostMapping("/student/create")
    public ResultVO<Void> createStudent(@RequestBody Student student) {
        if (student.getStudentId() == null || student.getStudentId().isEmpty()) {
            return ResultVO.error("学号不能为空");
        }
        if (student.getStudentName() == null || student.getStudentName().isEmpty()) {
            return ResultVO.error("姓名不能为空");
        }
        try {
            userService.createStudent(student); // 确保你的 userService 实现了这个方法
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 更新学生信息 (修改补全)
     */
    @PutMapping("/student/update")
    public ResultVO<Void> updateStudent(@RequestBody Student student) {
        try {
            userService.updateStudent(student); // 确保你的 userService 实现了这个方法
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 删除学生账号 (删除补全)
     */
    @DeleteMapping("/student/{id}")
    public ResultVO<Void> deleteStudent(@PathVariable String id) {
        try {
            userService.deleteStudent(id); // 确保你的 userService 实现了这个方法
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }
    // ==================== 组织机构 ====================

    /**
     * 获取所有组织机构（用于创建用户时选择归属组织）
     */
    @GetMapping("/org/list")
    public ResultVO<List<SysOrganization>> getOrgList() {
        List<SysOrganization> list = userService.getAllOrganizations();
        return ResultVO.success(list);
    }

    /**
     * 新增组织机构
     */
    @PostMapping("/org/create")
    public ResultVO<Void> createOrg(@RequestBody SysOrganization org) {
        try {
            userService.createOrganization(org);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 更新组织机构
     */
    @PutMapping("/org/update")
    public ResultVO<Void> updateOrg(@RequestBody SysOrganization org) {
        if (org.getOrgId() == null) {
            return ResultVO.error("组织ID不能为空");
        }
        try {
            userService.updateOrganization(org);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 删除组织机构
     */
    @DeleteMapping("/org/{orgId}")
    public ResultVO<Void> deleteOrg(@PathVariable Integer orgId) {
        try {
            userService.deleteOrganization(orgId);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }
}
