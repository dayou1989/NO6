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

@RestController
@RequestMapping("/tongzhijilu")
public class TongzhijiluController {
    @Autowired
    private TongzhijiluService tongzhijiluService;

    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, TongzhijiluEntity tongzhijilu,
                                        HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            tongzhijilu.setYishengzhanghao((String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            tongzhijilu.setZhanghao((String)request.getSession().getAttribute("username"));
        }
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        
        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", page);
    }

    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, TongzhijiluEntity tongzhijilu,
        HttpServletRequest request){
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();

        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", page);
    }

    @RequestMapping("/lists")
    public R list( TongzhijiluEntity tongzhijilu){
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        ew.allEq(MPUtil.allEQMapPre( tongzhijilu, "tongzhijilu")); 
        return R.ok().put("data", tongzhijiluService.selectListView(ew));
    }

    @RequestMapping("/query")
    public R query(TongzhijiluEntity tongzhijilu){
        EntityWrapper< TongzhijiluEntity> ew = new EntityWrapper< TongzhijiluEntity>();
        ew.allEq(MPUtil.allEQMapPre( tongzhijilu, "tongzhijilu")); 
        TongzhijiluView tongzhijiluView =  tongzhijiluService.selectView(ew);
        return R.ok("查询通知发送记录成功").put("data", tongzhijiluView);
    }
    
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
        tongzhijilu = tongzhijiluService.selectView(new EntityWrapper<TongzhijiluEntity>().eq("id", id));
        return R.ok().put("data", tongzhijilu);
    }

    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
        tongzhijilu = tongzhijiluService.selectView(new EntityWrapper<TongzhijiluEntity>().eq("id", id));
        return R.ok().put("data", tongzhijilu);
    }

    @RequestMapping("/save")
    @SysLog("新增通知发送记录")
    public R save(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request){
        tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }
    
    @SysLog("新增通知发送记录")
    @RequestMapping("/add")
    public R add(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request){
        tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }

    @RequestMapping("/update")
    @Transactional
    @SysLog("修改通知发送记录")
    public R update(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request){
        tongzhijiluService.updateById(tongzhijilu);
        return R.ok();
    }

    @RequestMapping("/delete")
    @SysLog("删除通知发送记录")
    public R delete(@RequestBody Long[] ids){
        tongzhijiluService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    @RequestMapping("/value/{xColumnName}/{yColumnName}")
    public R value(@PathVariable("yColumnName") String yColumnName, @PathVariable("xColumnName") String xColumnName,HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("xColumn", MPUtil.camelToSnake(xColumnName));
        params.put("yColumn", MPUtil.camelToSnake(yColumnName));
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            ew.eq("yishengzhanghao", (String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
        List<Map<String, Object>> result = MPUtil.snakeListToCamel(tongzhijiluService.selectValue(params, ew));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }

    @RequestMapping("/group/{columnName}")
    public R group(@PathVariable("columnName") String columnName,HttpServletRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("column", MPUtil.camelToSnake(columnName));
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            ew.eq("yishengzhanghao", (String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            ew.eq("zhanghao", (String)request.getSession().getAttribute("username"));
        }
        List<Map<String, Object>> result = MPUtil.snakeListToCamel(tongzhijiluService.selectGroup(params, ew));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for(Map<String, Object> m : result) {
            for(String k : m.keySet()) {
                if(m.get(k) instanceof Date) {
                    m.put(k, sdf.format((Date)m.get(k)));
                }
            }
        }
        return R.ok().put("data", result);
    }

    @RequestMapping("/count")
    public R count(@RequestParam Map<String, Object> params, TongzhijiluEntity tongzhijilu, HttpServletRequest request){
        String tableName = request.getSession().getAttribute("tableName").toString();
        if(tableName.equals("yisheng")) {
            tongzhijilu.setYishengzhanghao((String)request.getSession().getAttribute("username"));
        }
        if(tableName.equals("yonghu")) {
            tongzhijilu.setZhanghao((String)request.getSession().getAttribute("username"));
        }
        EntityWrapper<TongzhijiluEntity> ew = new EntityWrapper<TongzhijiluEntity>();
        int count = tongzhijiluService.selectCount(MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", count);
    }

    @RequestMapping("/retry/{id}")
    @SysLog("重试发送通知")
    public R retry(@PathVariable("id") Long id, HttpServletRequest request){
        TongzhijiluEntity record = tongzhijiluService.selectById(id);
        if(record == null) {
            return R.error("记录不存在");
        }
        if("成功".equals(record.getFasongzhuangtai())) {
            return R.error("该通知已发送成功，无需重试");
        }
        if(record.getChongshicishu() >= record.getZuidachongshi()) {
            return R.error("已达到最大重试次数");
        }
        record.setFasongzhuangtai("待发送");
        record.setXiacichongshishijian(new Date());
        tongzhijiluService.updateById(record);
        return R.ok("已加入重试队列");
    }

    @RequestMapping("/retryBatch")
    @SysLog("批量重试发送通知")
    public R retryBatch(@RequestBody Long[] ids, HttpServletRequest request){
        List<TongzhijiluEntity> list = new ArrayList<>();
        for(Long id : ids) {
            TongzhijiluEntity record = tongzhijiluService.selectById(id);
            if(record != null && !"成功".equals(record.getFasongzhuangtai()) 
                && record.getChongshicishu() < record.getZuidachongshi()) {
                record.setFasongzhuangtai("待发送");
                record.setXiacichongshishijian(new Date());
                list.add(record);
            }
        }
        if(!list.isEmpty()) {
            tongzhijiluService.updateBatchById(list);
        }
        return R.ok("已加入重试队列");
    }
}
