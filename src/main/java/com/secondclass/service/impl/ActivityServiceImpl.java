package com.secondclass.service.impl;

import com.secondclass.dto.ActivityCreateDTO;
import com.secondclass.entity.Activity;
import com.secondclass.mapper.ActivityMapper;
import com.secondclass.service.ActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<Activity> getAllActivities() {
        return activityMapper.selectAll();
    }

    @Override
    public void createActivity(ActivityCreateDTO dto) {
        Activity activity = new Activity();
        // 神器：一键把 DTO 里同名的属性拷贝到 Entity 里，不用手动一个个 set！
        BeanUtils.copyProperties(dto, activity);

        // 自动生成一个 32 位的 UUID 作为主键，去掉中间的横杠
        String uuid = UUID.randomUUID().toString().replace("-", "");
        activity.setActivityId(uuid);

        // 调用 Mapper 保存到数据库
        activityMapper.insert(activity);
    }
}