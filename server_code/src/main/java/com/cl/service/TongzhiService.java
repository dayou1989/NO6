package com.cl.service;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.entity.YonghuEntity;

import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 * 负责处理所有通知相关的业务逻辑
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhiService {

    /**
     * 用户接收渠道状态
     */
    class UserChannelStatus {
        private boolean smsEnabled;      // 短信渠道是否可用
        private boolean appEnabled;      // 应用内通知是否可用
        private boolean emailEnabled;    // 邮件渠道是否可用
        private String phoneNumber;      // 手机号
        private String email;            // 邮箱

        public boolean isSmsEnabled() {
            return smsEnabled;
        }

        public void setSmsEnabled(boolean smsEnabled) {
            this.smsEnabled = smsEnabled;
        }

        public boolean isAppEnabled() {
            return appEnabled;
        }

        public void setAppEnabled(boolean appEnabled) {
            this.appEnabled = appEnabled;
        }

        public boolean isEmailEnabled() {
            return emailEnabled;
        }

        public void setEmailEnabled(boolean emailEnabled) {
            this.emailEnabled = emailEnabled;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        /**
         * 检查是否有可用的通知渠道
         */
        public boolean hasAvailableChannel() {
            return smsEnabled || appEnabled || emailEnabled;
        }

        /**
         * 获取最佳通知渠道
         */
        public String getBestChannel() {
            if (appEnabled) return "app";
            if (smsEnabled) return "sms";
            if (emailEnabled) return "email";
            return null;
        }
    }

    /**
     * 通知发送结果
     */
    class SendResult {
        private boolean success;         // 是否发送成功
        private String channel;          // 使用的渠道
        private String message;          // 结果信息
        private String errorMsg;         // 错误信息（如果失败）

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    /**
     * 获取用户接收渠道状态
     * @param yonghu 用户实体
     * @return 渠道状态
     */
    UserChannelStatus getUserChannelStatus(YonghuEntity yonghu);

    /**
     * 预约成功后立即发送所有后续提醒
     * @param yuyue 预约实体
     * @param yonghu 用户实体
     * @return 发送结果列表
     */
    List<SendResult> sendAllNotificationsOnBooking(YishengyuyueEntity yuyue, YonghuEntity yonghu);

    /**
     * 发送单条通知
     * @param tongzhijilu 通知记录
     * @return 发送结果
     */
    SendResult sendNotification(TongzhijiluEntity tongzhijilu);

    /**
     * 重试发送通知
     * @param tongzhijilu 通知记录
     * @return 是否成功
     */
    boolean retrySendNotification(TongzhijiluEntity tongzhijilu);

    /**
     * 获取通知统计信息
     * @return 统计信息
     */
    Map<String, Object> getNotificationStatistics();

    /**
     * 创建通知记录
     * @param yuyuebianhao 预约编号
     * @param zhanghao 用户账号
     * @param shouji 手机号
     * @param tongzhileixing 通知类型
     * @param tongzhineirong 通知内容
     * @param fasongqudao 发送渠道
     * @param jihuafasongshijian 计划发送时间
     * @return 通知记录实体
     */
    TongzhijiluEntity createNotificationRecord(String yuyuebianhao, String zhanghao, String shouji,
                                               Integer tongzhileixing, String tongzhineirong,
                                               String fasongqudao, java.util.Date jihuafasongshijian);

    /**
     * 更新通知记录状态
     * @param id 记录ID
     * @param status 状态
     * @param errorMsg 错误信息
     */
    void updateNotificationStatus(Long id, Integer status, String errorMsg);
}
