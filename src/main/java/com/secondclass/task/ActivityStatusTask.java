package com.secondclass.task;

import com.secondclass.mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ActivityStatusTask {

    @Autowired
    private ActivityMapper activityMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void autoUpdateActivityStatus() {
        // 系统现在只负责一件事：监控活动是否开始
        int startCount = activityMapper.updateStatusToInProgress();
        if (startCount > 0) {
            System.out.println("🚀 [系统引擎] " + LocalDateTime.now() + " - 成功将 " + startCount + " 个活动自动切换为【活动进行中】");
        }
    }
}