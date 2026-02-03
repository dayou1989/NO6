package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;
import com.cl.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 通知记录
 *
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhijiluService extends IService<TongzhijiluEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);

    List<TongzhijiluView> selectListView(Wrapper<TongzhijiluEntity> wrapper);

    TongzhijiluView selectView(Wrapper<TongzhijiluEntity> wrapper);

    /**
     * 查询需要重试的通知记录
     */
    List<TongzhijiluEntity> selectRetryList(Integer maxRetryCount);
}
