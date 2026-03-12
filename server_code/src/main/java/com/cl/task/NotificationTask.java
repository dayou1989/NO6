package com.cl.task;

import com.cl.service.TongzhijiluService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 通知定时任务
 * 用于定时处理待发送的通知和重试失败的通知
 */
@Component
public class NotificationTask {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTask.class);

    @Autowired
    private TongzhijiluService tongzhijiluService;

    /**
     * 每分钟检查一次待发送的通知
     * 处理计划发送时间已到达的通知
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processPendingNotifications() {
        logger.info("开始执行待发送通知处理任务");
        try {
            tongzhijiluService.processPendingNotifications();
        } catch (Exception e) {
            logger.error("处理待发送通知时发生错误", e);
        }
        logger.info("待发送通知处理任务执行完成");
    }

    /**
     * 每5分钟重试一次发送失败的通知
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void retryFailedNotifications() {
        logger.info("开始执行失败通知重试任务");
        try {
            tongzhijiluService.retryFailedNotifications();
        } catch (Exception e) {
            logger.error("重试失败通知时发生错误", e);
        }
        logger.info("失败通知重试任务执行完成");
    }
}
