package com.cl.task;

import com.cl.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class NotificationRetryTask {

    @Autowired
    private NotificationService notificationService;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Scheduled(fixedRate = 300000)
    public void retryFailedNotifications() {
        System.out.println("[定时任务] 开始检查待发送通知 - " + sdf.format(new Date()));
        try {
            notificationService.retryFailedNotifications();
            System.out.println("[定时任务] 通知重试处理完成 - " + sdf.format(new Date()));
        } catch (Exception e) {
            System.err.println("[定时任务] 通知重试处理异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void sendScheduledReminders() {
        System.out.println("[定时任务] 开始处理定时提醒 - " + sdf.format(new Date()));
        try {
            System.out.println("[定时任务] 定时提醒处理完成 - " + sdf.format(new Date()));
        } catch (Exception e) {
            System.err.println("[定时任务] 定时提醒处理异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
