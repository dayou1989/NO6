package com.cl.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.Calendar;
import java.util.Date;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.JiuzhentongzhiDao;
import com.cl.entity.JiuzhentongzhiEntity;
import com.cl.service.JiuzhentongzhiService;
import com.cl.entity.view.JiuzhentongzhiView;

@Service("jiuzhentongzhiService")
public class JiuzhentongzhiServiceImpl extends ServiceImpl<JiuzhentongzhiDao, JiuzhentongzhiEntity> implements JiuzhentongzhiService {
    	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<JiuzhentongzhiEntity> page = this.selectPage(
                new Query<JiuzhentongzhiEntity>(params).getPage(),
                new EntityWrapper<JiuzhentongzhiEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<JiuzhentongzhiEntity> wrapper) {
		  Page<JiuzhentongzhiView> page =new Query<JiuzhentongzhiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<JiuzhentongzhiView> selectListView(Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public JiuzhentongzhiView selectView(Wrapper<JiuzhentongzhiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
	
	@Override
	public boolean sendNotification(Long id) {
		JiuzhentongzhiEntity notification = this.selectById(id);
		if (notification == null) {
			return false;
		}
		
		try {
			notification.setStatus("发送中");
			notification.setTongzhishijian(new Date());
			this.updateById(notification);
			
			boolean sendSuccess = doSendNotification(notification);
			
			if (sendSuccess) {
				notification.setStatus("发送成功");
				notification.setFailReason(null);
				this.updateById(notification);
				return true;
			} else {
				handleSendFailure(notification, "发送失败");
				return false;
			}
		} catch (Exception e) {
			handleSendFailure(notification, e.getMessage());
			return false;
		}
	}
	
	@Override
	public boolean retryNotification(Long id) {
		JiuzhentongzhiEntity notification = this.selectById(id);
		if (notification == null) {
			return false;
		}
		
		Integer retryCount = notification.getRetryCount();
		Integer maxRetryCount = notification.getMaxRetryCount();
		
		if (retryCount >= maxRetryCount) {
			notification.setStatus("发送失败");
			notification.setFailReason("已达到最大重试次数");
			this.updateById(notification);
			return false;
		}
		
		try {
			notification.setStatus("发送中");
			notification.setRetryCount(retryCount + 1);
			this.updateById(notification);
			
			boolean sendSuccess = doSendNotification(notification);
			
			if (sendSuccess) {
				notification.setStatus("发送成功");
				notification.setFailReason(null);
				this.updateById(notification);
				return true;
			} else {
				handleSendFailure(notification, "重试发送失败");
				return false;
			}
		} catch (Exception e) {
			handleSendFailure(notification, e.getMessage());
			return false;
		}
	}
	
	@Override
	public void processPendingNotifications() {
		List<JiuzhentongzhiEntity> pendingNotifications = getPendingNotifications();
		for (JiuzhentongzhiEntity notification : pendingNotifications) {
			sendNotification(notification.getId());
		}
		
		List<JiuzhentongzhiEntity> failedNotifications = getFailedNotifications();
		Date now = new Date();
		for (JiuzhentongzhiEntity notification : failedNotifications) {
			Date nextRetryTime = notification.getNextRetryTime();
			if (nextRetryTime != null && now.after(nextRetryTime)) {
				retryNotification(notification.getId());
			}
		}
	}
	
	@Override
	public List<JiuzhentongzhiEntity> getFailedNotifications() {
		EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("status", "发送失败");
		return this.selectList(wrapper);
	}
	
	@Override
	public List<JiuzhentongzhiEntity> getPendingNotifications() {
		EntityWrapper<JiuzhentongzhiEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("status", "待发送");
		return this.selectList(wrapper);
	}
	
	private boolean doSendNotification(JiuzhentongzhiEntity notification) {
		try {
			System.out.println("发送通知: " + notification.getTongzhibianhao() 
					+ " 给用户: " + notification.getZhanghao() 
					+ " 内容: " + notification.getTongzhibeizhu());
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void handleSendFailure(JiuzhentongzhiEntity notification, String reason) {
		Integer retryCount = notification.getRetryCount();
		Integer maxRetryCount = notification.getMaxRetryCount();
		
		if (retryCount >= maxRetryCount) {
			notification.setStatus("发送失败");
			notification.setFailReason(reason + "，已达到最大重试次数");
			notification.setNextRetryTime(null);
		} else {
			notification.setStatus("发送失败");
			notification.setFailReason(reason);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int delayMinutes = (int) Math.pow(2, retryCount) * 5;
			calendar.add(Calendar.MINUTE, delayMinutes);
			notification.setNextRetryTime(calendar.getTime());
		}
		
		this.updateById(notification);
	}
	


}
