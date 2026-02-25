package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.utils.PageUtils;
import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;

import java.util.List;
import java.util.Map;

public interface TongzhijiluService extends IService<TongzhijiluEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
    PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);
    
	List<TongzhijiluView> selectListView(Wrapper<TongzhijiluEntity> wrapper);

	TongzhijiluView selectView(Wrapper<TongzhijiluEntity> wrapper);

    List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);

    List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);

    List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<TongzhijiluEntity> wrapper);
}
