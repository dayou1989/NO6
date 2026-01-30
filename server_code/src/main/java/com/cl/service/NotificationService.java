package com.cl.service;

import com.cl.entity.TongzhiRecordEntity;
import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.entity.YonghuEntity;
import com.cl.entity.YishengEntity;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    @Autowired
    private TongzhiRecordService tongzhiRecordService;

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    @Autowired
    private YonghuService yonghuService;

    @Autowired
    private YishengService yishengService;

    private static final int MAX_RETRY_COUNT = 3;
    private static final String CHANNEL_SMS = "SMS";
    private static final String CHANNEL_EMAIL = "EMAIL";
    private static final String CHANNEL_SYSTEM = "SYSTEM";

    @Transactional
    public void sendNotificationForAppointment(YishengyuyueEntity appointment) {
        if (appointment == null) {
            return;
        }

        String userAccount = appointment.getZhanghao();
        YonghuEntity user = yonghuService.selectOne(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<YonghuEntity>()
                .eq("zhanghao", userAccount)
        );

        if (user == null) {
            return;
        }

        String doctorAccount = appointment.getYishengzhanghao();
        YishengEntity doctor = yishengService.selectOne(
            new com.baomidou.mybatisplus.mapper.EntityWrapper<YishengEntity>()
                .eq("yishengzhanghao", doctorAccount)
        );

        List<String> messages = generateNotificationMessages(appointment, user, doctor);

        for (String message : messages) {
            JiuzhentongzhiEntity notification = createNotification(appointment, doctor, user, message);
            jiuzhentongzhiService.insert(notification);

            sendThroughChannels(notification, user, message);
        }
    }

    private List<String> generateNotificationMessages(YishengyuyueEntity appointment, 
                                                        YonghuEntity user, 
                                                        YishengEntity doctor) {
        List<String> messages = new ArrayList<>();

        String doctorName = doctor != null ? doctor.getYishengxingming() : "医生";
        String userName = user.getYonghuxingming();
        String dateStr = appointment.getYuyueshijian() != null ? appointment.getYuyueshijian() : "";
        String timeSlot = appointment.getShangxiabanshijian() != null ? appointment.getShangxiabanshijian() : "";

        messages.add(String.format("【预约成功提醒】尊敬的%s，您已成功预约%s医生在%s %s的就诊，请准时到达。", 
            userName, doctorName, dateStr, timeSlot));

        messages.add(String.format("【就诊准备提醒】尊敬的%s，就诊当日请携带身份证、医保卡等相关证件，提前30分钟到达医院。", 
            userName));

        messages.add(String.format("【注意事项提醒】尊敬的%s，就诊前如有身体状况变化请及时告知医生，祝您早日康复。", 
            userName));

        return messages;
    }

    private JiuzhentongzhiEntity createNotification(YishengyuyueEntity appointment,
                                                     YishengEntity doctor,
                                                     YonghuEntity user,
                                                     String message) {
        JiuzhentongzhiEntity notification = new JiuzhentongzhiEntity();
        notification.setTongzhibianhao("TZ" + System.currentTimeMillis());
        notification.setYishengzhanghao(appointment.getYishengzhanghao());
        notification.setDianhua(doctor != null ? doctor.getYishengdianhua() : "");
        notification.setJiuzhenshijian(appointment.getYuyueshijian());
        notification.setTongzhishijian(new Date());
        notification.setZhanghao(appointment.getZhanghao());
        notification.setShouji(user.getShouji());
        notification.setTongzhibeizhu(message);
        notification.setAddtime(new Date());
        return notification;
    }

    private void sendThroughChannels(JiuzhentongzhiEntity notification, 
                                      YonghuEntity user, 
                                      String message) {
        if (user.getShouji() != null && !user.getShouji().isEmpty()) {
            sendSMS(notification.getId(), user.getShouji(), message);
        }

        if (user.getYouxiang() != null && !user.getYouxiang().isEmpty()) {
            sendEmail(notification.getId(), user.getYouxiang(), message);
        }

        sendSystemMessage(notification.getId(), user.getZhanghao(), message);
    }

    private void sendSMS(Long notificationId, String phoneNumber, String message) {
        TongzhiRecordEntity record = createRecord(notificationId, CHANNEL_SMS, phoneNumber, message);
        try {
            boolean isAvailable = checkSMSChannelAvailability(phoneNumber);
            if (!isAvailable) {
                record.setStatus("FAILED");
                record.setErrorMessage("短信渠道不可用 - 手机号格式不正确或运营商限制");
                tongzhiRecordService.insert(record);
                return;
            }

            boolean result = executeSMSSend(phoneNumber, message);
            if (result) {
                record.setStatus("SUCCESS");
                record.setErrorMessage(null);
            } else {
                record.setStatus("FAILED");
                record.setErrorMessage("短信发送失败 - 运营商返回失败");
            }
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setErrorMessage("短信发送异常: " + e.getMessage());
        }
        tongzhiRecordService.insert(record);
    }

    private void sendEmail(Long notificationId, String emailAddress, String message) {
        TongzhiRecordEntity record = createRecord(notificationId, CHANNEL_EMAIL, emailAddress, message);
        try {
            boolean isAvailable = checkEmailChannelAvailability(emailAddress);
            if (!isAvailable) {
                record.setStatus("FAILED");
                record.setErrorMessage("邮件渠道不可用 - 邮箱地址无效");
                tongzhiRecordService.insert(record);
                return;
            }

            boolean result = executeEmailSend(emailAddress, message);
            if (result) {
                record.setStatus("SUCCESS");
                record.setErrorMessage(null);
            } else {
                record.setStatus("FAILED");
                record.setErrorMessage("邮件发送失败 - 邮件服务器返回失败");
            }
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setErrorMessage("邮件发送异常: " + e.getMessage());
        }
        tongzhiRecordService.insert(record);
    }

    private void sendSystemMessage(Long notificationId, String account, String message) {
        TongzhiRecordEntity record = createRecord(notificationId, CHANNEL_SYSTEM, account, message);
        try {
            boolean result = executeSystemMessageSend(account, message);
            if (result) {
                record.setStatus("SUCCESS");
                record.setErrorMessage(null);
            } else {
                record.setStatus("FAILED");
                record.setErrorMessage("系统消息发送失败");
            }
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setErrorMessage("系统消息发送异常: " + e.getMessage());
        }
        tongzhiRecordService.insert(record);
    }

    private TongzhiRecordEntity createRecord(Long notificationId, String channelType, 
                                              String receiver, String content) {
        TongzhiRecordEntity record = new TongzhiRecordEntity();
        record.setTongzhiId(notificationId);
        record.setChannelType(channelType);
        record.setReceiver(receiver);
        record.setContent(content);
        record.setStatus("PENDING");
        record.setRetryCount(0);
        record.setMaxRetryCount(MAX_RETRY_COUNT);
        record.setAddtime(new Date());
        record.setUpdatetime(new Date());
        return record;
    }

    public boolean checkSMSChannelAvailability(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return phoneNumber.matches("^1[3-9]\\d{9}$");
    }

    public boolean checkEmailChannelAvailability(String emailAddress) {
        if (emailAddress == null || emailAddress.isEmpty()) {
            return false;
        }
        return emailAddress.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private boolean executeSMSSend(String phoneNumber, String message) {
        System.out.println("[模拟短信发送] 发送到: " + phoneNumber + ", 内容: " + message);
        return true;
    }

    private boolean executeEmailSend(String emailAddress, String message) {
        System.out.println("[模拟邮件发送] 发送到: " + emailAddress + ", 内容: " + message);
        return true;
    }

    private boolean executeSystemMessageSend(String account, String message) {
        System.out.println("[模拟系统消息发送] 发送给: " + account + ", 内容: " + message);
        return true;
    }

    public List<TongzhiRecordEntity> getFailedRecordsForRetry() {
        return tongzhiRecordService.selectFailedRecords(MAX_RETRY_COUNT);
    }

    @Transactional
    public void retryFailedNotification(TongzhiRecordEntity record) {
        if (record == null || !"FAILED".equals(record.getStatus())) {
            return;
        }

        record.setRetryCount(record.getRetryCount() + 1);
        record.setLastRetryTime(new Date());
        record.setUpdatetime(new Date());

        boolean result = false;
        try {
            if (CHANNEL_SMS.equals(record.getChannelType())) {
                result = executeSMSSend(record.getReceiver(), record.getContent());
            } else if (CHANNEL_EMAIL.equals(record.getChannelType())) {
                result = executeEmailSend(record.getReceiver(), record.getContent());
            } else if (CHANNEL_SYSTEM.equals(record.getChannelType())) {
                result = executeSystemMessageSend(record.getReceiver(), record.getContent());
            }

            if (result) {
                record.setStatus("SUCCESS");
                record.setErrorMessage(null);
            } else {
                record.setStatus("FAILED");
                record.setErrorMessage("重试失败");
            }
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setErrorMessage("重试异常: " + e.getMessage());
        }

        tongzhiRecordService.updateById(record);
    }
}
