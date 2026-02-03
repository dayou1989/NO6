package com.cl.entity.view;

import com.cl.entity.TongzhijiluEntity;

import java.io.Serializable;

/**
 * 通知记录视图对象
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
public class TongzhijiluView extends TongzhijiluEntity<TongzhijiluView> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户姓名
	 */
	private String xingming;

	/**
	 * 通知类型名称
	 */
	private String tongzhileixingName;

	/**
	 * 发送状态名称
	 */
	private String fasongzhuangtaiName;

	public String getXingming() {
		return xingming;
	}

	public void setXingming(String xingming) {
		this.xingming = xingming;
	}

	public String getTongzhileixingName() {
		return tongzhileixingName;
	}

	public void setTongzhileixingName(String tongzhileixingName) {
		this.tongzhileixingName = tongzhileixingName;
	}

	public String getFasongzhuangtaiName() {
		return fasongzhuangtaiName;
	}

	public void setFasongzhuangtaiName(String fasongzhuangtaiName) {
		this.fasongzhuangtaiName = fasongzhuangtaiName;
	}
}
