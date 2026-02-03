package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.TongzhijiluDao;
import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;
import com.cl.service.TongzhijiluService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 通知记录
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@Service("tongzhijiluService")
public class TongzhijiluServiceImpl extends ServiceImpl<TongzhijiluDao, TongzhijiluEntity> implements TongzhijiluService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TongzhijiluEntity> page = this.selectPage(
                new Query<TongzhijiluEntity>(params).getPage(),
                new com.baomidou.mybatisplus.mapper.EntityWrapper<TongzhijiluEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper) {
        Page<TongzhijiluView> page = new Query<TongzhijiluView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
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
    public List<TongzhijiluEntity> selectRetryList(Integer maxRetryCount) {
        return baseMapper.selectRetryList(maxRetryCount);
    }
}
