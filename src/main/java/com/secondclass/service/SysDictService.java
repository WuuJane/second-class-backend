package com.secondclass.service;

import com.secondclass.entity.SysDict;
import java.util.List;

public interface SysDictService {
    List<SysDict> getDictByType(String dictType);
    void addDict(SysDict sysDict);
    void updateDict(SysDict sysDict);
    void changeStatus(Integer id, Integer status);
}