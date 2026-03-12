package com.cl;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.YishengyuyueEntity;
import com.cl.service.NotificationService;
import com.cl.service.TongzhijiluService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通知服务测试类
 */
@SpringBootTest
@Transactional
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TongzhijiluService tongzhijiluService;

    /**
     * 测试预约审核通过后创建通知
     */
    @Test
    public void testCreateNotificationsAfterApproval() {
        // 创建测试预约数据
        YishengyuyueEntity yuyue = new YishengyuyueEntity();
        yuyue.setYuyuebianhao("TEST" + System.currentTimeMillis());
        yuyue.setZhanghao("testUser");
        yuyue.setYishengzhanghao("testDoctor");
        
        // 设置预约时间为3天后，确保24小时和1小时提醒都会创建
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        yuyue.setYuyueshijian(calendar.getTime());

        // 调用创建通知方法
        notificationService.createNotificationsAfterApproval(yuyue);

        // 验证是否创建了4个通知
        List<TongzhijiluEntity> notifications = tongzhijiluService.selectList(null);
        long count = notifications.stream()
                .filter(n -> yuyue.getYuyuebianhao().equals(n.getYuyuebianhao()))
                .count();
        
        assertEquals(4, count, "应该创建4个通知记录（预约成功、24小时提醒、1小时提醒、就诊通知）");
    }

    /**
     * 测试通知发送功能
     */
    @Test
    public void testSendNotification() {
        // 创建测试通知
        TongzhijiluEntity notification = new TongzhijiluEntity();
        notification.setTongzhibianhao("TZ" + System.currentTimeMillis());
        notification.setYuyuebianhao("TEST123");
        notification.setZhanghao("testUser");
        notification.setYishengzhanghao("testDoctor");
        notification.setTongzhileixing(1);
        notification.setTongzhineirong("测试通知内容");
        notification.setFasongzhuangtai(0);
        notification.setJieshouzhuangtai(0);
        notification.setChongshicishu(0);
        notification.setJihuafasongshijian(new Date());

        tongzhijiluService.insert(notification);

        // 验证通知是否保存成功
        assertNotNull(notification.getId(), "通知ID不应为空");
        
        // 查询验证
        TongzhijiluEntity saved = tongzhijiluService.selectById(notification.getId());
        assertNotNull(saved, "保存的通知应该能查询到");
        assertEquals("testUser", saved.getZhanghao(), "用户账号应该匹配");
    }

    /**
     * 测试标记通知为已接收
     */
    @Test
    public void testMarkAsReceived() {
        // 创建测试通知
        TongzhijiluEntity notification = new TongzhijiluEntity();
        notification.setTongzhibianhao("TZ" + System.currentTimeMillis());
        notification.setYuyuebianhao("TEST123");
        notification.setZhanghao("testUser");
        notification.setTongzhileixing(1);
        notification.setFasongzhuangtai(1);
        notification.setJieshouzhuangtai(0);
        notification.setJihuafasongshijian(new Date());

        tongzhijiluService.insert(notification);

        // 标记为已接收
        boolean result = tongzhijiluService.markAsReceived(notification.getId());
        
        assertTrue(result, "标记应该成功");
        
        // 验证状态
        TongzhijiluEntity updated = tongzhijiluService.selectById(notification.getId());
        assertEquals(Integer.valueOf(1), updated.getJieshouzhuangtai(), "接收状态应该是1");
        assertNotNull(updated.getJieshoushijian(), "接收时间不应为空");
    }

    /**
     * 测试取消通知
     */
    @Test
    public void testCancelNotifications() {
        String yuyuebianhao = "TEST" + System.currentTimeMillis();
        
        // 创建测试通知
        for (int i = 0; i < 3; i++) {
            TongzhijiluEntity notification = new TongzhijiluEntity();
            notification.setTongzhibianhao("TZ" + System.currentTimeMillis() + i);
            notification.setYuyuebianhao(yuyuebianhao);
            notification.setZhanghao("testUser");
            notification.setTongzhileixing(i + 1);
            notification.setFasongzhuangtai(0);
            notification.setJihuafasongshijian(new Date());
            tongzhijiluService.insert(notification);
        }

        // 取消通知
        notificationService.cancelNotifications(yuyuebianhao);

        // 验证通知状态已更新为取消(3)
        List<TongzhijiluEntity> notifications = tongzhijiluService.selectList(null);
        long cancelledCount = notifications.stream()
                .filter(n -> yuyuebianhao.equals(n.getYuyuebianhao()) && n.getFasongzhuangtai() == 3)
                .count();
        
        assertEquals(3, cancelledCount, "应该有3个通知被标记为取消");
    }
}
