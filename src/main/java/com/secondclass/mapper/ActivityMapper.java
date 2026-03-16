package com.secondclass.mapper;

import com.secondclass.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ActivityMapper {
    // 之前写的查询
    List<Activity> selectAll();

    int insert(Activity activity);
}