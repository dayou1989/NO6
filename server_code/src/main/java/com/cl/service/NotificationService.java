package com.cl.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.entity.*;
import com.cl.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NotificationService {

    @Autowired
    private TongzhijiluService tongzhijiluService;

    @Autowired
    private JiuzhentongzhiService jiuzhentongzhiService;

    @Autowired
    private YonghuService yonghuService;

    @Autowired
    private YishengService yishengService;

    @Autowired
    private MessagesService messagesService;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public void sendAppointmentNotification(YishengyuyueEntity yuyue) {
        YonghuEntity yonghu = yonghuService.selectOne(
            new EntityWrapper<YonghuEntity>().eq("zhanghao", yuyue.getZhanghao())
        );
        
        String tongzhibianhao = String.valueOf(System.currentTimeMillis());
        String tongzhineirong = buildNotificationContent(yuyue);
        
        JiuzhentongzhiEntity tongzhi = new JiuzhentongzhiEntity();
        tongzhi.setTongzhibianhao(tongzhibianhao);
        tongzhi.setYishengzhanghao(yuyue.getYishengzhanghao());
        tongzhi.setDianhua(yuyue.getDianhua());
        tongzhi.setJiuzhenshijian(yuyue.getYuyueshijian());
        tongzhi.setTongzhishijian(new Date());
        tongzhi.setZhanghao(yuyue.getZhanghao());
        tongzhi.setShouji(yuyue.getShouji());
        tongzhi.setTongzhibeizhu("预约成功通知：" + tongzhineirong);
        jiuzhentongzhiService.insert(tongzhi);

        boolean smsEnabled = true;
        boolean messageEnabled = true;
        if (yonghu != null) {
            smsEnabled = yonghu.getSmsEnabled() != null && yonghu.getSmsEnabled() == 1;
            messageEnabled = yonghu.getMessageEnabled() != null && yonghu.getMessageEnabled() == 1;
        }

        if (smsEnabled) {
            createNotificationRecord(yuyue, tongzhibianhao, tongzhineirong, "sms");
        }

        if (messageEnabled) {
            createNotificationRecord(yuyue, tongzhibianhao, tongzhineirong, "站内信");
        }
    }

    private void createNotificationRecord(YishengyuyueEntity yuyue, String tongzhibianhao, 
                                          String tongzhineirong, String qudao) {
        TongzhijiluEntity record = new TongzhijiluEntity();
        record.setTongzhibianhao(tongzhibianhao);
        record.setYuyuebianhao(yuyue.getYuyuebianhao());
        record.setYishengzhanghao(yuyue.getYishengzhanghao());
        record.setDianhua(yuyue.getDianhua());
        record.setZhanghao(yuyue.getZhanghao());
        record.setShouji(yuyue.getShouji());
        record.setJiuzhenshijian(yuyue.getYuyueshijian());
        record.setTongzhineirong(tongzhineirong);
        record.setFasongqudao(qudao);
        record.setFasongzhuangtai("待发送");
        record.setChongshicishu(0);
        record.setZuidachongshi(3);
        record.setXiacichongshishijian(new Date());
        
        tongzhijiluService.insert(record);
        
        processNotification(record);
    }

    private String buildNotificationContent(YishengyuyueEntity yuyue) {
        StringBuilder sb = new StringBuilder();
        sb.append("尊敬的用户，您已成功预约医生【").append(yuyue.getYishengzhanghao()).append("】");
        sb.append("，预约时间：").append(sdf.format(yuyue.getYuyueshijian()));
        sb.append("，请按时就诊。如有变动请及时联系医生，电话：").append(yuyue.getDianhua());
        return sb.toString();
    }

    public void processNotification(TongzhijiluEntity record) {
        try {
            boolean success = false;
            String failReason = null;

            if ("站内信".equals(record.getFasongqudao())) {
                success = sendInternalMessage(record);
                if (!success) {
                    failReason = "站内信发送失败";
                }
            } else if ("sms".equals(record.getFasongqudao())) {
                success = sendSms(record);
                if (!success) {
                    failReason = "短信发送失败：手机号无效或服务不可用";
                }
            }

            if (success) {
                record.setFasongzhuangtai("成功");
                record.setFasongshijian(new Date());
            } else {
                record.setFasongzhuangtai("失败");
                record.setShibaoyuanyin(failReason);
                record.setChongshicishu(record.getChongshicishu() + 1);
                if (record.getChongshicishu() < record.getZuidachongshi()) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MINUTE, 5);
                    record.setXiacichongshishijian(cal.getTime());
                } else {
                    record.setXiacichongshishijian(null);
                }
            }
            
            tongzhijiluService.updateById(record);
            
        } catch (Exception e) {
            record.setFasongzhuangtai("失败");
            record.setShibaoyuanyin("发送异常：" + e.getMessage());
            record.setChongshicishu(record.getChongshicishu() + 1);
            tongzhijiluService.updateById(record);
        }
    }

    private boolean sendInternalMessage(TongzhijiluEntity record) {
        try {
            YonghuEntity yonghu = yonghuService.selectOne(
                new EntityWrapper<YonghuEntity>().eq("zhanghao", record.getZhanghao())
            );
            
            MessagesEntity message = new MessagesEntity();
            if (yonghu != null) {
                message.setUserid(yonghu.getId());
                message.setUsername(yonghu.getXingming());
                message.setAvatarurl(yonghu.getTouxiang());
            }
            message.setContent("【就诊通知】" + record.getTongzhineirong());
            message.setAddtime(new Date());
            messagesService.insert(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean sendSms(TongzhijiluEntity record) {
        String phone = record.getShouji();
        if (phone == null || phone.length() != 11) {
            return false;
        }
        
        return true;
    }

    public void retryFailedNotifications() {
        List<TongzhijiluEntity> failedList = tongzhijiluService.selectList(
            new EntityWrapper<TongzhijiluEntity>()
                .eq("fasongzhuangtai", "待发送")
                .le("chongshicishu", 3)
                .isNotNull("xiacichongshishijian")
                .le("xiacichongshishijian", new Date())
        );

        for (TongzhijiluEntity record : failedList) {
            processNotification(record);
        }
    }

    public void sendAllReminders(YishengyuyueEntity yuyue) {
        sendAppointmentNotification(yuyue);
        
        sendReminder(yuyue, "就诊前24小时提醒", 24);
        sendReminder(yuyue, "就诊前2小时提醒", 2);
    }

    private void sendReminder(YishengyuyueEntity yuyue, String reminderType, int hoursBefore) {
        YonghuEntity yonghu = yonghuService.selectOne(
            new EntityWrapper<YonghuEntity>().eq("zhanghao", yuyue.getZhanghao())
        );
        
        String tongzhibianhao = System.currentTimeMillis() + "_" + hoursBefore;
        String tongzhineirong = String.format("【%s】您预约的医生【%s】就诊时间为%s，请提前做好准备。", 
            reminderType, yuyue.getYishengzhanghao(), sdf.format(yuyue.getYuyueshijian()));
        
        boolean smsEnabled = yonghu == null || (yonghu.getSmsEnabled() != null && yonghu.getSmsEnabled() == 1);
        boolean messageEnabled = yonghu == null || (yonghu.getMessageEnabled() != null && yonghu.getMessageEnabled() == 1);

        if (smsEnabled) {
            TongzhijiluEntity record = new TongzhijiluEntity();
            record.setTongzhibianhao(tongzhibianhao);
            record.setYuyuebianhao(yuyue.getYuyuebianhao());
            record.setYishengzhanghao(yuyue.getYishengzhanghao());
            record.setDianhua(yuyue.getDianhua());
            record.setZhanghao(yuyue.getZhanghao());
            record.setShouji(yuyue.getShouji());
            record.setJiuzhenshijian(yuyue.getYuyueshijian());
            record.setTongzhineirong(tongzhineirong);
            record.setFasongqudao("sms");
            record.setFasongzhuangtai("待发送");
            record.setChongshicishu(0);
            record.setZuidachongshi(3);
            record.setBeizhu(reminderType);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(yuyue.getYuyueshijian());
            cal.add(Calendar.HOUR, -hoursBefore);
            record.setXiacichongshishijian(cal.getTime());
            
            tongzhijiluService.insert(record);
        }

        if (messageEnabled) {
            TongzhijiluEntity record = new TongzhijiluEntity();
            record.setTongzhibianhao(tongzhibianhao + "_msg");
            record.setYuyuebianhao(yuyue.getYuyuebianhao());
            record.setYishengzhanghao(yuyue.getYishengzhanghao());
            record.setDianhua(yuyue.getDianhua());
            record.setZhanghao(yuyue.getZhanghao());
            record.setShouji(yuyue.getShouji());
            record.setJiuzhenshijian(yuyue.getYuyueshijian());
            record.setTongzhineirong(tongzhineirong);
            record.setFasongqudao("站内信");
            record.setFasongzhuangtai("待发送");
            record.setChongshicishu(0);
            record.setZuidachongshi(3);
            record.setBeizhu(reminderType);
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(yuyue.getYuyueshijian());
            cal.add(Calendar.HOUR, -hoursBefore);
            record.setXiacichongshishijian(cal.getTime());
            
            tongzhijiluService.insert(record);
        }
    }
}
