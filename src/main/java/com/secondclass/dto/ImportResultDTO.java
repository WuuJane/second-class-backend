package com.secondclass.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入结果
 */
@Data
public class ImportResultDTO {
    private int total;          // 总行数
    private int success;        // 成功导入数
    private int fail;           // 失败数
    private List<String> errors = new ArrayList<>(); // 错误详情
}
