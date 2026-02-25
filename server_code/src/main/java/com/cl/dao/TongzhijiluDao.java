package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;

public interface TongzhijiluDao extends BaseMapper<TongzhijiluEntity> {
	
	List<TongzhijiluView> selectListView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<TongzhijiluEntity> wrapper);

	List<TongzhijiluView> selectListView(@Param("page") com.baomidou.mybatisplus.plugins.Page<TongzhijiluView> page, @Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<TongzhijiluEntity> wrapper);
	
	TongzhijiluView selectView(@Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<TongzhijiluEntity> wrapper);

    List<Map<String, Object>> selectValue(@Param("params") Map<String, Object> params, @Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<TongzhijiluEntity> wrapper);

    List<Map<String, Object>> selectTimeStatValue(@Param("params") Map<String, Object> params, @Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<TongzhijiluEntity> wrapper);

    List<Map<String, Object>> selectGroup(@Param("params") Map<String, Object> params, @Param("ew") com.baomidou.mybatisplus.mapper.Wrapper<TongzhijiluEntity> wrapper);
}
