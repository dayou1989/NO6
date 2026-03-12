package com.cl.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.Date;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.TongzhijiluDao;
import com.cl.entity.TongzhijiluEntity;
import com.cl.service.TongzhijiluService;
import com.cl.entity.view.TongzhijiluView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service("tongzhijiluService")
public class TongzhijiluServiceImpl extends ServiceImpl<TongzhijiluDao, TongzhijiluEntity> implements TongzhijiluService {

    private static final Logger logger = LoggerFactory.getLogger(TongzhijiluServiceImpl.class);
    
    @Autowired
    private TongzhijiluDao tongzhijiluDao;
    
    // 最大重试次数
    private static final int MAX_RETRY_COUNT = 3;
    
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TongzhijiluEntity> page = this.selectPage(
                new Query<TongzhijiluEntity>(params).getPage(),
                new EntityWrapper<TongzhijiluEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper) {
		  Page<TongzhijiluView> page =new Query<TongzhijiluView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<TongzhijiluView> selectListView(Wrapper<TongzhijiluEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public TongzhijiluView selectView(Wrapper<TongzhijiluEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
	
	@Override
	public List<TongzhijiluEntity> selectFailedNotifications(Integer maxRetryCount) {
		return tongzhijiluDao.selectFailedNotifications(maxRetryCount);
	}

	@Override
	public List<TongzhijiluEntity> selectPendingNotifications() {
		return tongzhijiluDao.selectPendingNotifications();
	}
	
	@Override
	@Transactional
	public void retryFailedNotifications() {
		List<TongzhijiluEntity> failedList = selectFailedNotifications(MAX_RETRY_COUNT);
		for (TongzhijiluEntity notification : failedList) {
			try {
				sendNotification(notification);
			} catch (Exception e) {
				logger.error("重试发送通知失败，id: " + notification.getId(), e);
				// 更新重试次数
				Integer retryCount = notification.getChongshicishu();
				if (retryCount == null) {
					retryCount = 0;
				}
				notification.setChongshicishu(retryCount + 1);
				notification.setShibaiyuanyin(e.getMessage());
				updateById(notification);
			}
		}
	}
	
	@Override
	@Transactional
	public void processPendingNotifications() {
		List<TongzhijiluEntity> pendingList = selectPendingNotifications();
		for (TongzhijiluEntity notification : pendingList) {
			try {
				sendNotification(notification);
			} catch (Exception e) {
				logger.error("发送通知失败，id: " + notification.getId(), e);
				notification.setFasongzhuangtai(2); // 发送失败
				notification.setShibaiyuanyin(e.getMessage());
				Integer retryCount = notification.getChongshicishu();
				if (retryCount == null) {
					retryCount = 0;
				}
				notification.setChongshicishu(retryCount + 1);
				updateById(notification);
			}
		}
	}
	
	@Override
	@Transactional
	public boolean markAsReceived(Long id) {
		TongzhijiluEntity notification = selectById(id);
		if (notification == null) {
			return false;
		}
		notification.setJieshouzhuangtai(1);
		notification.setJieshoushijian(new Date());
		return updateById(notification);
	}
	
	@Override
	@Transactional
	public boolean manualRetry(Long id) {
		TongzhijiluEntity notification = selectById(id);
		if (notification == null) {
			return false;
		}
		try {
			sendNotification(notification);
			return true;
		} catch (Exception e) {
			logger.error("手动重试发送通知失败，id: " + id, e);
			notification.setShibaiyuanyin(e.getMessage());
			Integer retryCount = notification.getChongshicishu();
			if (retryCount == null) {
				retryCount = 0;
			}
			notification.setChongshicishu(retryCount + 1);
			updateById(notification);
			return false;
		}
	}
	
	@Override
	@Transactional
	public void sendNotificationNow(TongzhijiluEntity notification) throws Exception {
		sendNotification(notification);
	}
	
	/**
	 * 发送通知的实际方法
	 */
	private void sendNotification(TongzhijiluEntity notification) throws Exception {
		// 检查通知是否已取消
		if (notification.getFasongzhuangtai() != null && notification.getFasongzhuangtai() == 3) {
			logger.info("通知已取消，跳过发送: " + notification.getId());
			return;
		}
		
		// 这里可以实现实际的通知发送逻辑
		// 例如：发送短信、推送消息、站内信等
		// 目前使用模拟发送
		
		logger.info("发送通知给用户: " + notification.getZhanghao() + 
				", 类型: " + notification.getTongzhileixing() + 
				", 内容: " + notification.getTongzhineirong());
		
		// 模拟发送过程（实际项目中这里调用短信接口或推送服务）
		boolean sendSuccess = simulateSend(notification);
		
		if (sendSuccess) {
			notification.setFasongzhuangtai(1); // 发送成功
			notification.setFasongshijian(new Date());
			notification.setShibaiyuanyin(null);
		} else {
			throw new RuntimeException("通知服务返回发送失败");
		}
		
		updateById(notification);
	}
	
	/**
	 * 模拟发送通知（实际项目中替换为真实的发送逻辑）
	 */
	private boolean simulateSend(TongzhijiluEntity notification) {
		// 模拟发送，随机成功或失败（90%成功率）
		// 实际项目中这里应该调用真实的短信或推送服务
		return Math.random() > 0.1;
	}

}
