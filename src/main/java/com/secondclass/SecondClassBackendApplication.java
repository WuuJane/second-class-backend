package com.secondclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 💡 必须引入这个包

@SpringBootApplication
@EnableScheduling // 💡 给系统装上“电池”，开启定时任务引擎
public class SecondClassBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondClassBackendApplication.class, args);
    }

}