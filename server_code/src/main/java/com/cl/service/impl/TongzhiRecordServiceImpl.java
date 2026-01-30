package com.cl.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;
import java.util.Date;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;


import com.cl.dao.TongzhiRecordDao;
import com.cl.entity.TongzhiRecordEntity;
import com.cl.service.TongzhiRecordService;
import com.cl.entity.view.TongzhiRecordView;

@Service("tongzhiRecordService")
public class TongzhiRecordServiceImpl extends ServiceImpl<TongzhiRecordDao, TongzhiRecordEntity> implements TongzhiRecordService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TongzhiRecordEntity> page = this.selectPage(
                new Query<TongzhiRecordEntity>(params).getPage(),
                new EntityWrapper<TongzhiRecordEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<TongzhiRecordEntity> wrapper) {
		  Page<TongzhiRecordView> page =new Query<TongzhiRecordView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<TongzhiRecordView> selectListView(Wrapper<TongzhiRecordEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public TongzhiRecordView selectView(Wrapper<TongzhiRecordEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

	@Override
	public List<TongzhiRecordEntity> selectFailedRecords(Integer maxRetryCount) {
		return baseMapper.selectFailedRecords(maxRetryCount);
	}

	@Override
	public boolean updateRecordStatus(Long id, String status, String errorMessage) {
		TongzhiRecordEntity record = this.selectById(id);
		if (record != null) {
			record.setStatus(status);
			record.setErrorMessage(errorMessage);
			record.setUpdatetime(new Date());
			return this.updateById(record);
		}
		return false;
	}

	@Override
	public boolean incrementRetryCount(Long id) {
		TongzhiRecordEntity record = this.selectById(id);
		if (record != null) {
			record.setRetryCount(record.getRetryCount() + 1);
			record.setLastRetryTime(new Date());
			record.setUpdatetime(new Date());
			return this.updateById(record);
		}
		return false;
	}

}
