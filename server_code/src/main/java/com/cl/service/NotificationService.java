package com.cl.service;

import com.cl.entity.YishengyuyueEntity;

/**
 * 通知服务接口
 * 用于处理预约相关的通知逻辑
 */
public interface NotificationService {
    
    /**
     * 预约审核通过后，立即创建所有后续通知记录
     * @param yuyue 预约信息
     */
    void createNotificationsAfterApproval(YishengyuyueEntity yuyue);
    
    /**
     * 取消预约时，取消所有相关的待发送通知
     * @param yuyuebianhao 预约编号
     */
    void cancelNotifications(String yuyuebianhao);
}
