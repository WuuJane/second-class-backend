package com.secondclass.service.impl;

import com.secondclass.entity.SysDict;
import com.secondclass.mapper.SysDictMapper;
import com.secondclass.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDictServiceImpl implements SysDictService {

    @Autowired
    private SysDictMapper sysDictMapper;

    @Override
    public List<SysDict> getDictByType(String dictType) {
        // 只查询 status = 1 (正常启用) 的字典项，并且按 sort_order 排序
        return sysDictMapper.selectByType(dictType);
    }

    @Override
    public void addDict(SysDict sysDict) {
        // 校验同一个类型下，值（Value）不能重复
        SysDict existDict = sysDictMapper.checkValueExist(sysDict.getDictType(), sysDict.getDictValue());
        if (existDict != null) {
            throw new RuntimeException("该字典类型下已存在相同的键值！");
        }

        // 设置默认值
        if (sysDict.getStatus() == null) sysDict.setStatus(1);
        if (sysDict.getSortOrder() == null) sysDict.setSortOrder(0);

        sysDictMapper.insertDict(sysDict);
    }

    @Override
    public void updateDict(SysDict sysDict) {
        sysDictMapper.updateDict(sysDict);
    }

    @Override
    public void changeStatus(Integer id, Integer status) {
        sysDictMapper.updateStatus(id, status);
    }
}