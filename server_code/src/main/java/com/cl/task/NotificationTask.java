package com.cl.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cl.service.JiuzhentongzhiService;

@Component
public class NotificationTask {

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    @Scheduled(fixedRate = 60000)
    public void processNotifications() {
        try {
            jiuzhentongzhiService.processPendingNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
