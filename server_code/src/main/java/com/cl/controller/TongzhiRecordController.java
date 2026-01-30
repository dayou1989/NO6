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

import com.cl.entity.TongzhiRecordEntity;
import com.cl.entity.view.TongzhiRecordView;

import com.cl.service.TongzhiRecordService;
import com.cl.service.TokenService;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import com.cl.utils.MPUtil;
import com.cl.utils.MapUtils;
import com.cl.utils.CommonUtil;

@RestController
@RequestMapping("/tongzhirecord")
public class TongzhiRecordController {
    @Autowired
    private TongzhiRecordService tongzhiRecordService;

    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, TongzhiRecordEntity tongzhiRecord,
                                                                                                                                                    HttpServletRequest request){
        EntityWrapper<TongzhiRecordEntity> ew = new EntityWrapper<TongzhiRecordEntity>();
        PageUtils page = tongzhiRecordService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhiRecord), params), params));
        return R.ok().put("data", page);
    }

	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, TongzhiRecordEntity tongzhiRecord,
		HttpServletRequest request){
        EntityWrapper<TongzhiRecordEntity> ew = new EntityWrapper<TongzhiRecordEntity>();
		PageUtils page = tongzhiRecordService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhiRecord), params), params));
        return R.ok().put("data", page);
    }

    @RequestMapping("/lists")
    public R list( TongzhiRecordEntity tongzhiRecord){
       	EntityWrapper<TongzhiRecordEntity> ew = new EntityWrapper<TongzhiRecordEntity>();
      	ew.allEq(MPUtil.allEQMapPre( tongzhiRecord, "tongzhiRecord")); 
        return R.ok().put("data", tongzhiRecordService.selectListView(ew));
    }

    @RequestMapping("/query")
    public R query(TongzhiRecordEntity tongzhiRecord){
        EntityWrapper<TongzhiRecordEntity> ew = new EntityWrapper<TongzhiRecordEntity>();
 		ew.allEq(MPUtil.allEQMapPre( tongzhiRecord, "tongzhiRecord")); 
		TongzhiRecordView tongzhiRecordView =  tongzhiRecordService.selectView(ew);
		return R.ok("查询通知记录成功").put("data", tongzhiRecordView);
    }
	
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        TongzhiRecordEntity tongzhiRecord = tongzhiRecordService.selectById(id);
		tongzhiRecord = tongzhiRecordService.selectView(new EntityWrapper<TongzhiRecordEntity>().eq("id", id));
        return R.ok().put("data", tongzhiRecord);
    }

	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        TongzhiRecordEntity tongzhiRecord = tongzhiRecordService.selectById(id);
		tongzhiRecord = tongzhiRecordService.selectView(new EntityWrapper<TongzhiRecordEntity>().eq("id", id));
        return R.ok().put("data", tongzhiRecord);
    }

    @RequestMapping("/save")
    @SysLog("新增通知记录")
    public R save(@RequestBody TongzhiRecordEntity tongzhiRecord, HttpServletRequest request){
        tongzhiRecordService.insert(tongzhiRecord);
        return R.ok();
    }
    
    @RequestMapping("/add")
    @SysLog("新增通知记录")
    public R add(@RequestBody TongzhiRecordEntity tongzhiRecord, HttpServletRequest request){
        tongzhiRecordService.insert(tongzhiRecord);
        return R.ok();
    }

    @RequestMapping("/update")
    @Transactional
    @SysLog("修改通知记录")
    public R update(@RequestBody TongzhiRecordEntity tongzhiRecord, HttpServletRequest request){
        tongzhiRecordService.updateById(tongzhiRecord);
        return R.ok();
    }

    @RequestMapping("/delete")
    @SysLog("删除通知记录")
    public R delete(@RequestBody Long[] ids){
        tongzhiRecordService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    @RequestMapping("/failed")
    @SysLog("查看失败通知列表")
    public R failedList(@RequestParam Map<String, Object> params, 
                        @RequestParam(defaultValue = "3") Integer maxRetryCount,
                        HttpServletRequest request){
        EntityWrapper<TongzhiRecordEntity> ew = new EntityWrapper<TongzhiRecordEntity>();
        ew.eq("status", "FAILED");
        if(params.get("channelType") != null && StringUtils.isNotBlank(params.get("channelType").toString())) {
            ew.eq("channelType", params.get("channelType").toString());
        }
        ew.orderBy("addtime", false);
        PageUtils page = tongzhiRecordService.queryPage(params, ew);
        return R.ok().put("data", page);
    }

    @RequestMapping("/retry/{id}")
    @Transactional
    @SysLog("手动重试发送通知")
    public R retry(@PathVariable("id") Long id, HttpServletRequest request){
        TongzhiRecordEntity record = tongzhiRecordService.selectById(id);
        if(record == null) {
            return R.error("记录不存在");
        }
        if(!"FAILED".equals(record.getStatus())) {
            return R.error("只有失败状态的记录可以重试");
        }
        if(record.getRetryCount() >= record.getMaxRetryCount()) {
            return R.error("已达到最大重试次数");
        }
        record.setStatus("PENDING");
        record.setRetryCount(record.getRetryCount() + 1);
        record.setLastRetryTime(new Date());
        record.setUpdatetime(new Date());
        tongzhiRecordService.updateById(record);
        return R.ok();
    }

    @RequestMapping("/batchRetry")
    @Transactional
    @SysLog("批量重试发送通知")
    public R batchRetry(@RequestBody Long[] ids, HttpServletRequest request){
        for(Long id : ids) {
            TongzhiRecordEntity record = tongzhiRecordService.selectById(id);
            if(record != null && "FAILED".equals(record.getStatus()) 
                && record.getRetryCount() < record.getMaxRetryCount()) {
                record.setStatus("PENDING");
                record.setRetryCount(record.getRetryCount() + 1);
                record.setLastRetryTime(new Date());
                record.setUpdatetime(new Date());
                tongzhiRecordService.updateById(record);
            }
        }
        return R.ok();
    }

    @RequestMapping("/statistics")
    public R statistics(HttpServletRequest request){
        Map<String, Object> result = new HashMap<>();
        
        EntityWrapper<TongzhiRecordEntity> successEw = new EntityWrapper<>();
        successEw.eq("status", "SUCCESS");
        int successCount = tongzhiRecordService.selectCount(successEw);
        
        EntityWrapper<TongzhiRecordEntity> failedEw = new EntityWrapper<>();
        failedEw.eq("status", "FAILED");
        int failedCount = tongzhiRecordService.selectCount(failedEw);
        
        EntityWrapper<TongzhiRecordEntity> pendingEw = new EntityWrapper<>();
        pendingEw.eq("status", "PENDING");
        int pendingCount = tongzhiRecordService.selectCount(pendingEw);
        
        EntityWrapper<TongzhiRecordEntity> smsEw = new EntityWrapper<>();
        smsEw.eq("channelType", "SMS");
        int smsCount = tongzhiRecordService.selectCount(smsEw);
        
        EntityWrapper<TongzhiRecordEntity> emailEw = new EntityWrapper<>();
        emailEw.eq("channelType", "EMAIL");
        int emailCount = tongzhiRecordService.selectCount(emailEw);
        
        result.put("successCount", successCount);
        result.put("failedCount", failedCount);
        result.put("pendingCount", pendingCount);
        result.put("smsCount", smsCount);
        result.put("emailCount", emailCount);
        result.put("totalCount", successCount + failedCount + pendingCount);
        
        return R.ok().put("data", result);
    }

}
