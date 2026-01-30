package com.cl.task;

import com.cl.entity.TongzhiRecordEntity;
import com.cl.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationRetryTask {

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 60000)
    public void retryFailedNotifications() {
        List<TongzhiRecordEntity> failedRecords = notificationService.getFailedRecordsForRetry();
        
        if (failedRecords == null || failedRecords.isEmpty()) {
            return;
        }

        for (TongzhiRecordEntity record : failedRecords) {
            notificationService.retryFailedNotification(record);
        }
    }
}
