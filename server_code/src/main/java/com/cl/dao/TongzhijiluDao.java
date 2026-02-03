package com.cl.dao;

import com.cl.entity.TongzhijiluEntity;
import com.cl.entity.view.TongzhijiluView;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知记录
 * 
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public interface TongzhijiluDao extends BaseMapper<TongzhijiluEntity> {

	List<TongzhijiluView> selectListView(@Param("ew") Wrapper<TongzhijiluEntity> wrapper);

	List<TongzhijiluView> selectListView(Pagination page, @Param("ew") Wrapper<TongzhijiluEntity> wrapper);

	TongzhijiluView selectView(@Param("ew") Wrapper<TongzhijiluEntity> wrapper);

	/**
	 * 查询需要重试的通知记录
	 */
	List<TongzhijiluEntity> selectRetryList(@Param("maxRetryCount") Integer maxRetryCount);
}
