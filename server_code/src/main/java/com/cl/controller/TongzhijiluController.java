package com.cl.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.cl.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.annotation.SysLog;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;

import com.cl.service.TongzhijiluService;
import com.cl.service.TokenService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.MapUtils;
import com.cl.utils.CommonUtil;

/**
 * 通知记录
 * 后端接口
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@RestController
@RequestMapping("/tongzhijilu")
public class TongzhijiluController {
    @Autowired
    private TongzhijiluService tongzhijiluService;

    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,TongzhijiluEntity tongzhijilu,
            HttpServletRequest request){
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        
        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,TongzhijiluEntity tongzhijilu,
		HttpServletRequest request){
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();

		PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( TongzhijiluEntity tongzhijilu){
       	EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
      	ew.allEq(MPUtil.allEQMapPre( tongzhijilu, "tongzhijilu")); 
        return R.ok().put("data", tongzhijiluService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(TongzhijiluEntity tongzhijilu){
        EntityWrapper< TongzhijiluEntity> ew = new EntityWrapper< TongzhijiluEntity>();
 		ew.allEq(MPUtil.allEQMapPre( tongzhijilu, "tongzhijilu")); 
		TongzhijiluView tongzhijiluView =  tongzhijiluService.selectView(ew);
		return R.ok("查询通知记录成功").put("data", tongzhijiluView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
		tongzhijilu = tongzhijiluService.selectView(new EntityWrapper<TongzhijiluEntity>().eq("id", id));
        return R.ok().put("data", tongzhijilu);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
		tongzhijilu = tongzhijiluService.selectView(new EntityWrapper<TongzhijiluEntity>().eq("id", id));
        return R.ok().put("data", tongzhijilu);
    }
    

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    @SysLog("新增通知记录")
    public R save(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request){
    	tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @SysLog("新增通知记录")
    @RequestMapping("/add")
    public R add(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request){
    	tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @SysLog("修改通知记录")
    public R update(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request){
        tongzhijiluService.updateById(tongzhijilu);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @SysLog("删除通知记录")
    public R delete(@RequestBody Long[] ids){
        tongzhijiluService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * 标记通知为已接收
     */
    @RequestMapping("/receive/{id}")
    @SysLog("标记通知为已接收")
    public R receive(@PathVariable("id") Long id){
        boolean success = tongzhijiluService.markAsReceived(id);
        if (success) {
            return R.ok("标记成功");
        } else {
            return R.error("标记失败，通知不存在");
        }
    }
    
    /**
     * 手动重试发送通知
     */
    @RequestMapping("/retry/{id}")
    @SysLog("手动重试发送通知")
    public R retry(@PathVariable("id") Long id){
        boolean success = tongzhijiluService.manualRetry(id);
        if (success) {
            return R.ok("重试发送成功");
        } else {
            return R.error("重试发送失败");
        }
    }
    
    /**
     * 批量重试发送失败的通知
     */
    @RequestMapping("/retryFailed")
    @SysLog("批量重试发送失败的通知")
    public R retryFailed(){
        tongzhijiluService.retryFailedNotifications();
        return R.ok("批量重试任务已执行");
    }
    
    /**
     * 获取发送失败的通知列表
     */
    @RequestMapping("/failedList")
    public R failedList(@RequestParam(defaultValue = "3") Integer maxRetryCount){
        List<TongzhijiluEntity> list = tongzhijiluService.selectFailedNotifications(maxRetryCount);
        return R.ok().put("data", list);
    }
    
    /**
     * 获取待发送的通知列表
     */
    @RequestMapping("/pendingList")
    public R pendingList(){
        List<TongzhijiluEntity> list = tongzhijiluService.selectPendingNotifications();
        return R.ok().put("data", list);
    }
    
    /**
     * 获取通知统计信息
     */
    @RequestMapping("/statistics")
    public R statistics(){
        Map<String, Object> statistics = new HashMap<>();
        
        // 待发送
        Wrapper<TongzhijiluEntity> pendingWrapper = new EntityWrapper<>();
        pendingWrapper.eq("fasongzhuangtai", 0);
        int pendingCount = tongzhijiluService.selectCount(pendingWrapper);
        statistics.put("pendingCount", pendingCount);
        
        // 发送成功
        Wrapper<TongzhijiluEntity> successWrapper = new EntityWrapper<>();
        successWrapper.eq("fasongzhuangtai", 1);
        int successCount = tongzhijiluService.selectCount(successWrapper);
        statistics.put("successCount", successCount);
        
        // 发送失败
        Wrapper<TongzhijiluEntity> failedWrapper = new EntityWrapper<>();
        failedWrapper.eq("fasongzhuangtai", 2);
        int failedCount = tongzhijiluService.selectCount(failedWrapper);
        statistics.put("failedCount", failedCount);
        
        // 已接收
        Wrapper<TongzhijiluEntity> receivedWrapper = new EntityWrapper<>();
        receivedWrapper.eq("jieshouzhuangtai", 1);
        int receivedCount = tongzhijiluService.selectCount(receivedWrapper);
        statistics.put("receivedCount", receivedCount);
        
        // 未接收
        Wrapper<TongzhijiluEntity> unreceivedWrapper = new EntityWrapper<>();
        unreceivedWrapper.eq("jieshouzhuangtai", 0);
        int unreceivedCount = tongzhijiluService.selectCount(unreceivedWrapper);
        statistics.put("unreceivedCount", unreceivedCount);
        
        return R.ok().put("data", statistics);
    }
    
    /**
     * 获取用户的通知列表（前端接口）
     */
    @RequestMapping("/myNotifications")
    public R myNotifications(@RequestParam Map<String, Object> params, HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        String username = (String)request.getSession().getAttribute("username");
        
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", username);
        }
        
        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(ew, params));
        return R.ok().put("data", page);
    }
    
    /**
     * 用户标记通知为已读（前端接口）
     */
    @RequestMapping("/markAsRead/{id}")
    public R markAsRead(@PathVariable("id") Long id, HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        
        // 只允许用户标记自己的通知
        if(!tableName.equals("yonghu")) {
            return R.error("只有用户可以标记通知");
        }
        
        String username = (String)request.getSession().getAttribute("username");
        TongzhijiluEntity notification = tongzhijiluService.selectById(id);
        
        if(notification == null) {
            return R.error("通知不存在");
        }
        
        if(!username.equals(notification.getZhanghao())) {
            return R.error("只能标记自己的通知");
        }
        
        boolean success = tongzhijiluService.markAsReceived(id);
        if (success) {
            return R.ok("标记成功");
        } else {
            return R.error("标记失败");
        }
    }
    
    /**
     * 获取用户未读通知数量（前端接口）
     */
    @RequestMapping("/unreadCount")
    public R unreadCount(HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        
        if(!tableName.equals("yonghu")) {
            return R.ok().put("data", 0);
        }
        
        String username = (String)request.getSession().getAttribute("username");
        
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        ew.eq("zhanghao", username);
        ew.eq("jieshouzhuangtai", 0);
        ew.eq("fasongzhuangtai", 1); // 只统计已发送成功的
        
        int count = tongzhijiluService.selectCount(ew);
        return R.ok().put("data", count);
    }
}
