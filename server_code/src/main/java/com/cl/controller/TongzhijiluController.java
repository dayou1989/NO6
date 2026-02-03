package com.cl.controller;

import com.cl.annotation.SysLog;
import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;
import com.cl.service.TongzhijiluService;
import com.cl.service.TongzhiService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

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

    @Autowired
    private TongzhiService tongzhiService;

    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, TongzhijiluEntity tongzhijilu,
                  HttpServletRequest request) {
        com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity> ew = new com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity>();

        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, TongzhijiluEntity tongzhijilu,
                  HttpServletRequest request) {
        com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity> ew = new com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity>();

        PageUtils page = tongzhijiluService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, tongzhijilu), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(TongzhijiluEntity tongzhijilu) {
        com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity> ew = new com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity>();
        ew.allEq(MPUtil.allEQMapPre(tongzhijilu, "tongzhijilu"));
        return R.ok().put("data", tongzhijiluService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(TongzhijiluEntity tongzhijilu) {
        com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity> ew = new com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity>();
        ew.allEq(MPUtil.allEQMapPre(tongzhijilu, "tongzhijilu"));
        TongzhijiluView tongzhijiluView = tongzhijiluService.selectView(ew);
        return R.ok("查询通知记录成功").put("data", tongzhijiluView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
        tongzhijilu = tongzhijiluService.selectView(new com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity>().eq("id", id));
        return R.ok().put("data", tongzhijilu);
    }

    /**
     * 前端详情
     */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
        tongzhijilu = tongzhijiluService.selectView(new com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity>().eq("id", id));
        return R.ok().put("data", tongzhijilu);
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    @SysLog("新增通知记录")
    public R save(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request) {
        tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @SysLog("新增通知记录")
    @RequestMapping("/add")
    public R add(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request) {
        tongzhijiluService.insert(tongzhijilu);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @SysLog("修改通知记录")
    public R update(@RequestBody TongzhijiluEntity tongzhijilu, HttpServletRequest request) {
        tongzhijiluService.updateById(tongzhijilu);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @SysLog("删除通知记录")
    public R delete(@RequestBody Long[] ids) {
        tongzhijiluService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 重试发送通知
     */
    @RequestMapping("/retry/{id}")
    @Transactional
    @SysLog("重试发送通知")
    public R retry(@PathVariable("id") Long id, HttpServletRequest request) {
        TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
        if (tongzhijilu == null) {
            return R.error("通知记录不存在");
        }

        // 调用通知服务重试发送
        boolean success = tongzhiService.retrySendNotification(tongzhijilu);

        if (success) {
            return R.ok("重试发送成功");
        } else {
            return R.error("重试发送失败");
        }
    }

    /**
     * 批量重试发送
     */
    @RequestMapping("/retryBatch")
    @Transactional
    @SysLog("批量重试发送通知")
    public R retryBatch(@RequestBody Long[] ids, HttpServletRequest request) {
        int successCount = 0;
        int failCount = 0;

        for (Long id : ids) {
            TongzhijiluEntity tongzhijilu = tongzhijiluService.selectById(id);
            if (tongzhijilu != null) {
                boolean success = tongzhiService.retrySendNotification(tongzhijilu);
                if (success) {
                    successCount++;
                } else {
                    failCount++;
                }
            } else {
                failCount++;
            }
        }

        return R.ok("批量重试完成，成功：" + successCount + "，失败：" + failCount);
    }

    /**
     * 获取发送统计
     */
    @RequestMapping("/statistics")
    public R statistics(HttpServletRequest request) {
        Map<String, Object> statistics = tongzhiService.getNotificationStatistics();
        return R.ok().put("data", statistics);
    }
}
