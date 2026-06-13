package com.secondclass.service.impl;

import com.secondclass.dto.ImportResultDTO;
import com.secondclass.entity.Admin;
import com.secondclass.entity.SysOrganization;
import com.secondclass.entity.Student;
import com.secondclass.entity.TUser;
import com.secondclass.mapper.AdminMapper;
import com.secondclass.mapper.StudentMapper;
import com.secondclass.mapper.SysOrganizationMapper;
import com.secondclass.mapper.UserMapper;
import com.secondclass.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

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

    // ========== 学生管理 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createStudent(Student student) {
        Student existing = studentMapper.selectById(student.getStudentId());
        if (existing != null) {
            throw new RuntimeException("学号 " + student.getStudentId() + " 已存在！");
        }
        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            student.setPassword("123456");
        }
        studentMapper.insert(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(Student student) {
        Student existing = studentMapper.selectById(student.getStudentId());
        if (existing == null) {
            throw new RuntimeException("学生不存在！");
        }
        studentMapper.update(student);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(String id) {
        Student existing = studentMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("学生不存在！");
        }
        studentMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetStudentPassword(String studentId) {
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在！");
        }
        studentMapper.updatePassword(studentId, "123456");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultDTO importStudentsFromExcel(MultipartFile file) {
        ImportResultDTO result = new ImportResultDTO();
        List<String> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int total = 0, success = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第1行开始，跳过表头
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String studentId = getCellStr(row.getCell(0));
                String studentName = getCellStr(row.getCell(1));
                String department = getCellStr(row.getCell(2));
                String grade = getCellStr(row.getCell(3));

                if (studentId.isEmpty()) continue;
                total++;

                try {
                    Student s = new Student();
                    s.setStudentId(studentId);
                    s.setStudentName(studentName.isEmpty() ? "未命名" : studentName);
                    s.setStudentDepartment(department);
                    s.setGrade(grade);
                    s.setPassword("123456");

                    Student existing = studentMapper.selectById(studentId);
                    if (existing != null) {
                        errors.add("第" + (i + 1) + "行 " + studentId + ": 学号已存在");
                        continue;
                    }
                    studentMapper.insert(s);
                    success++;
                } catch (RuntimeException e) {
                    errors.add("第" + (i + 1) + "行 " + studentId + ": " + e.getMessage());
                }
            }

            result.setTotal(total);
            result.setSuccess(success);
            result.setFail(total - success);
            result.setErrors(errors);
        } catch (Exception e) {
            result.getErrors().add("文件解析失败：" + e.getMessage());
        }

        return result;
    }

    private String getCellStr(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.getStringCellValue().trim();
    }
}
