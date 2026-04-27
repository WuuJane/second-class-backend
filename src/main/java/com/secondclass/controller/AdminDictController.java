package com.secondclass.controller;

import com.secondclass.common.ResultVO;
import com.secondclass.entity.SysDict;
import com.secondclass.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dict")
public class AdminDictController {

    @Autowired
    private SysDictService sysDictService;

    /**
     * 1. 查：根据字典类型获取对应的字典列表（给前端下拉框用的核心接口）
     * @param dictType 如：'activity_status'
     */
    @GetMapping("/list")
    public ResultVO<List<SysDict>> getDictList(@RequestParam String dictType) {
        List<SysDict> list = sysDictService.getDictByType(dictType);
        return ResultVO.success(list);
    }

    /**
     * 2. 增：添加字典项
     */
    @PostMapping("/add")
    public ResultVO<Void> addDict(@RequestBody SysDict sysDict) {
        try {
            sysDictService.addDict(sysDict);
            return ResultVO.success();
        } catch (RuntimeException e) {
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 3. 改：修改字典项
     */
    @PutMapping("/update")
    public ResultVO<Void> updateDict(@RequestBody SysDict sysDict) {
        if (sysDict.getId() == null) {
            return ResultVO.error("字典ID不能为空");
        }
        sysDictService.updateDict(sysDict);
        return ResultVO.success();
    }

    /**
     * 4. 停用/启用字典项
     */
    @PutMapping("/status")
    public ResultVO<Void> changeDictStatus(@RequestParam Integer id, @RequestParam Integer status) {
        sysDictService.changeStatus(id, status);
        return ResultVO.success();
    }
}