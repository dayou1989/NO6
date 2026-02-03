package com.cl.task;

import com.cl.entity.TongzhijiluEntity;
import com.cl.service.TongzhijiluService;
import com.cl.service.TongzhiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 通知定时任务
 * 用于定时检查并发送待发送的通知
 * @author
 * @email
 * @date 2025-03-27 15:44:15
 */
@Component
public class NotificationTask {

    @Autowired
    private TongzhijiluService tongzhijiluService;

    @Autowired
    private TongzhiService tongzhiService;

    /**
     * 每分钟检查一次待发送的通知
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkAndSendPendingNotifications() {
        System.out.println("【定时任务】检查待发送通知...");

        // 查询所有待发送且计划发送时间已到的通知
        com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity> wrapper =
                new com.baomidou.mybatisplus.mapper.EntityWrapper<>();
        wrapper.eq("fasongzhuangtai", 0); // 待发送
        wrapper.le("jihuafasongshijian", new Date()); // 计划发送时间已到
        wrapper.orderBy("jihuafasongshijian", true); // 按时间升序

        List<TongzhijiluEntity> pendingList = tongzhijiluService.selectList(wrapper);

        if (pendingList != null && !pendingList.isEmpty()) {
            System.out.println("【定时任务】发现 " + pendingList.size() + " 条待发送通知");

            for (TongzhijiluEntity record : pendingList) {
                try {
                    System.out.println("【定时任务】发送通知 ID: " + record.getId());
                    TongzhiService.SendResult result = tongzhiService.sendNotification(record);

                    if (result.isSuccess()) {
                        System.out.println("【定时任务】通知发送成功 ID: " + record.getId());
                    } else {
                        System.err.println("【定时任务】通知发送失败 ID: " + record.getId() + ", 原因: " + result.getErrorMsg());
                    }
                } catch (Exception e) {
                    System.err.println("【定时任务】发送通知异常 ID: " + record.getId() + ", 异常: " + e.getMessage());
                }
            }
        } else {
            System.out.println("【定时任务】没有待发送的通知");
        }
    }

    /**
     * 每5分钟检查一次需要重试的失败通知
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void retryFailedNotifications() {
        System.out.println("【定时任务】检查失败通知重试...");

        // 查询需要重试的通知（发送失败且重试次数未达到最大值）
        List<TongzhijiluEntity> retryList = tongzhijiluService.selectRetryList(3);

        if (retryList != null && !retryList.isEmpty()) {
            System.out.println("【定时任务】发现 " + retryList.size() + " 条需要重试的通知");

            for (TongzhijiluEntity record : retryList) {
                try {
                    System.out.println("【定时任务】重试发送通知 ID: " + record.getId());
                    boolean success = tongzhiService.retrySendNotification(record);

                    if (success) {
                        System.out.println("【定时任务】通知重试成功 ID: " + record.getId());
                    } else {
                        System.err.println("【定时任务】通知重试失败 ID: " + record.getId());
                    }
                } catch (Exception e) {
                    System.err.println("【定时任务】重试通知异常 ID: " + record.getId() + ", 异常: " + e.getMessage());
                }
            }
        } else {
            System.out.println("【定时任务】没有需要重试的通知");
        }
    }

    /**
     * 每天凌晨清理超过30天的成功通知记录
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanOldNotifications() {
        System.out.println("【定时任务】清理过期通知记录...");

        // 计算30天前的日期
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();

        // 删除30天前且发送成功的记录
        com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity> wrapper =
                new com.baomidou.mybatisplus.mapper.EntityWrapper<>();
        wrapper.eq("fasongzhuangtai", 1); // 发送成功
        wrapper.lt("shijifasongshijian", thirtyDaysAgo); // 30天前

        boolean result = tongzhijiluService.delete(wrapper);

        if (result) {
            System.out.println("【定时任务】清理过期通知记录完成");
        }
    }
}
