package com.secondclass.service.impl;

import com.secondclass.entity.Admin;
import com.secondclass.entity.SysOrganization;
import com.secondclass.entity.Student;
import com.secondclass.entity.TUser;
import com.secondclass.mapper.AdminMapper;
import com.secondclass.mapper.StudentMapper;
import com.secondclass.mapper.SysOrganizationMapper;
import com.secondclass.mapper.UserMapper;
import com.secondclass.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public void createStudent(Student student) {
        // 调用 StudentMapper 的插入方法
        studentMapper.insert(student);
    }

    @Override
    public void updateStudent(Student student) {
        // 调用 StudentMapper 的动态更新方法
        studentMapper.update(student);
    }

    @Override
    public void deleteStudent(String id) {
        // 根据你的数据库主键或者学号删除，这里参考传过来的 String id
        // 如果你的 Mapper 里面是用主键删除，通常叫 deleteById
        studentMapper.deleteById(id);
    }

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private SysOrganizationMapper sysOrganizationMapper;

    @Override
    public TUser getUserById(String id) {
        return userMapper.selectById(id);
    }

    @Override
    public Student getStudentById(String studentId) {
        return studentMapper.selectById(studentId);
    }

    // ========== 教职工管理 ==========

    @Override
    public List<TUser> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(TUser user) {
        // 检查工号是否已存在
        TUser existing = userMapper.selectById(user.getId());
        if (existing != null) {
            throw new RuntimeException("工号 " + user.getId() + " 已存在，请勿重复添加！");
        }
        // 默认启用状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 默认密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(TUser user) {
        TUser existing = userMapper.selectById(user.getId());
        if (existing == null) {
            throw new RuntimeException("用户不存在！");
        }
        userMapper.update(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String id) {
        TUser existing = userMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("用户不存在！");
        }
        userMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleUserStatus(String id) {
        TUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在！");
        }
        Integer newStatus = (user.getStatus() != null && user.getStatus() == 1) ? 0 : 1;
        userMapper.updateStatus(id, newStatus);
    }

    // ========== 管理员账号管理 ==========

    @Override
    public List<Admin> getAllAdmins() {
        return adminMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAdmin(Admin admin) {
        // 检查工号是否已存在
        Admin existing = adminMapper.selectByWorkNo(admin.getWorkNo());
        if (existing != null) {
            throw new RuntimeException("工号 " + admin.getWorkNo() + " 已存在！");
        }
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            admin.setPassword("123456");
        }
        adminMapper.insert(admin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdmin(Admin admin) {
        adminMapper.update(admin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdmin(Long id) {
        adminMapper.deleteById(id);
    }

    // ========== 学生管理 ==========

    @Override
    public List<Student> getAllStudents() {
        return studentMapper.selectAll();
    }

    // ========== 组织机构 ==========

    @Override
    public List<SysOrganization> getAllOrganizations() {
        return sysOrganizationMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrganization(SysOrganization org) {
        if (org.getOrgName() == null || org.getOrgName().isEmpty()) {
            throw new RuntimeException("组织名称不能为空");
        }
        if (org.getOrgLevel() == null || org.getOrgLevel().isEmpty()) {
            throw new RuntimeException("组织级别不能为空");
        }
        if (org.getCollegeName() == null || org.getCollegeName().isEmpty()) {
            throw new RuntimeException("所属学院不能为空");
        }
        if (org.getOrgType() == null || org.getOrgType().isEmpty()) {
            throw new RuntimeException("组织类型不能为空");
        }
        sysOrganizationMapper.insert(org);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrganization(SysOrganization org) {
        SysOrganization existing = sysOrganizationMapper.selectById(org.getOrgId());
        if (existing == null) {
            throw new RuntimeException("组织机构不存在！");
        }
        sysOrganizationMapper.update(org);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrganization(Integer orgId) {
        SysOrganization existing = sysOrganizationMapper.selectById(orgId);
        if (existing == null) {
            throw new RuntimeException("组织机构不存在！");
        }
        sysOrganizationMapper.deleteById(orgId);
    }
}
