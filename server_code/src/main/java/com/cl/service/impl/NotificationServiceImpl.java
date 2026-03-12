package com.cl.service.impl;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.NotificationService;
import com.cl.service.TongzhijiluService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.mapper.EntityWrapper;

/**
 * 通知服务实现类
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    @Autowired
    private TongzhijiluService tongzhijiluService;
    
    // 通知类型常量
    private static final int NOTIFICATION_TYPE_APPOINTMENT_SUCCESS = 1;  // 预约成功通知
    private static final int NOTIFICATION_TYPE_24H_REMINDER = 2;         // 就诊前24小时提醒
    private static final int NOTIFICATION_TYPE_1H_REMINDER = 3;          // 就诊前1小时提醒
    private static final int NOTIFICATION_TYPE_APPOINTMENT_TIME = 4;     // 就诊时间通知
    
    @Override
    @Transactional
    public void createNotificationsAfterApproval(YishengyuyueEntity yuyue) {
        if (yuyue == null || yuyue.getYuyueshijian() == null) {
            logger.error("预约信息为空或预约时间为空，无法创建通知");
            return;
        }
        
        Date appointmentTime = yuyue.getYuyueshijian();
        String yuyuebianhao = yuyue.getYuyuebianhao();
        String zhanghao = yuyue.getZhanghao();
        String yishengzhanghao = yuyue.getYishengzhanghao();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(appointmentTime);
        
        Date now = new Date();
        
        // 1. 创建预约成功通知（立即发送）
        createAndSendNotification(
            yuyuebianhao,
            zhanghao,
            yishengzhanghao,
            NOTIFICATION_TYPE_APPOINTMENT_SUCCESS,
            "您的预约已成功审核通过！预约时间：" + timeStr + "，医生账号：" + yishengzhanghao,
            now
        );
        
        // 2. 创建就诊前24小时提醒（立即发送）
        createAndSendNotification(
            yuyuebianhao,
            zhanghao,
            yishengzhanghao,
            NOTIFICATION_TYPE_24H_REMINDER,
            "就诊提醒：您预约的就诊将在24小时后（" + timeStr + "）开始，请做好准备。",
            now
        );
        
        // 3. 创建就诊前1小时提醒（立即发送）
        createAndSendNotification(
            yuyuebianhao,
            zhanghao,
            yishengzhanghao,
            NOTIFICATION_TYPE_1H_REMINDER,
            "就诊提醒：您预约的就诊将在1小时后（" + timeStr + "）开始，请尽快前往。",
            now
        );
        
        // 4. 创建就诊时间通知（立即发送）
        createAndSendNotification(
            yuyuebianhao,
            zhanghao,
            yishengzhanghao,
            NOTIFICATION_TYPE_APPOINTMENT_TIME,
            "就诊通知：您预约的就诊时间为" + timeStr + "，请准时前往就诊。",
            now
        );
        
        logger.info("已为预约 {} 创建并尝试发送所有通知", yuyuebianhao);
    }
    
    @Override
    @Transactional
    public void cancelNotifications(String yuyuebianhao) {
        if (yuyuebianhao == null) {
            return;
        }
        
        // 查询该预约的所有待发送通知
        EntityWrapper<TongzhijiluEntity> wrapper = new EntityWrapper<>();
        wrapper.eq("yuyuebianhao", yuyuebianhao);
        wrapper.eq("fasongzhuangtai", 0); // 待发送状态
        
        List<TongzhijiluEntity> pendingNotifications = tongzhijiluService.selectList(wrapper);
        
        // 删除或标记为取消
        for (TongzhijiluEntity notification : pendingNotifications) {
            notification.setFasongzhuangtai(3); // 3-已取消
            tongzhijiluService.updateById(notification);
        }
        
        logger.info("已取消预约 {} 的所有待发送通知，共 {} 条", yuyuebianhao, pendingNotifications.size());
    }
    
    /**
     * 创建单个通知记录并立即发送
     */
    private void createAndSendNotification(String yuyuebianhao, String zhanghao, String yishengzhanghao,
                                   int type, String content, Date scheduledTime) {
        try {
            TongzhijiluEntity notification = new TongzhijiluEntity();
            notification.setYuyuebianhao(yuyuebianhao);
            notification.setZhanghao(zhanghao);
            notification.setYishengzhanghao(yishengzhanghao);
            notification.setTongzhileixing(type);
            notification.setTongzhineirong(content);
            notification.setFasongzhuangtai(0); // 待发送
            notification.setJieshouzhuangtai(0); // 未接收
            notification.setChongshicishu(0);
            notification.setJihuafasongshijian(scheduledTime);
            
            // 生成通知编号
            notification.setTongzhibianhao(generateNotificationNo());
            
            // 先插入到数据库
            tongzhijiluService.insert(notification);
            
            logger.info("创建通知成功：类型={}, 计划发送时间={}", type, scheduledTime);
            
            // 立即尝试发送通知
            try {
                tongzhijiluService.sendNotificationNow(notification);
            } catch (Exception e) {
                logger.error("立即发送通知失败，已保存失败记录：类型=" + type, e);
            }
        } catch (Exception e) {
            logger.error("创建通知失败：类型=" + type, e);
        }
    }
    
    /**
     * 计算指定时间前的小时数
     */
    private Date calculateTimeBefore(Date baseTime, int hoursBefore) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseTime);
        calendar.add(Calendar.HOUR_OF_DAY, -hoursBefore);
        return calendar.getTime();
    }
    
    /**
     * 生成通知编号
     */
    private String generateNotificationNo() {
        return "TZ" + System.currentTimeMillis();
    }
}
