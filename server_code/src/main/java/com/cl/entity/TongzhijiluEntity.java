package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 通知记录
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@TableName("tongzhijilu")
public class TongzhijiluEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public TongzhijiluEntity() {
		
	}
	
	public TongzhijiluEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	
	/**
	 * 通知编号
	 */
	private String tongzhibianhao;
	
	/**
	 * 预约编号
	 */
	private String yuyuebianhao;
	
	/**
	 * 用户账号
	 */
	private String zhanghao;
	
	/**
	 * 医生账号
	 */
	private String yishengzhanghao;
	
	/**
	 * 通知类型（1-预约成功通知，2-就诊前24小时提醒，3-就诊前1小时提醒，4-就诊时间通知）
	 */
	private Integer tongzhileixing;
	
	/**
	 * 通知内容
	 */
	private String tongzhineirong;
	
	/**
	 * 发送状态（0-待发送，1-发送成功，2-发送失败）
	 */
	private Integer fasongzhuangtai;
	
	/**
	 * 发送时间
	 */
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat		
	private Date fasongshijian;
	
	/**
	 * 接收状态（0-未接收，1-已接收）
	 */
	private Integer jieshouzhuangtai;
	
	/**
	 * 接收时间
	 */
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat		
	private Date jieshoushijian;
	
	/**
	 * 重试次数
	 */
	private Integer chongshicishu;
	
	/**
	 * 失败原因
	 */
	private String shibaiyuanyin;
	
	/**
	 * 计划发送时间
	 */
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat		
	private Date jihuafasongshijian;

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTongzhibianhao() {
		return tongzhibianhao;
	}

	public void setTongzhibianhao(String tongzhibianhao) {
		this.tongzhibianhao = tongzhibianhao;
	}
	
	public String getYuyuebianhao() {
		return yuyuebianhao;
	}

	public void setYuyuebianhao(String yuyuebianhao) {
		this.yuyuebianhao = yuyuebianhao;
	}
	
	public String getZhanghao() {
		return zhanghao;
	}

	public void setZhanghao(String zhanghao) {
		this.zhanghao = zhanghao;
	}
	
	public String getYishengzhanghao() {
		return yishengzhanghao;
	}

	public void setYishengzhanghao(String yishengzhanghao) {
		this.yishengzhanghao = yishengzhanghao;
	}
	
	public Integer getTongzhileixing() {
		return tongzhileixing;
	}

	public void setTongzhileixing(Integer tongzhileixing) {
		this.tongzhileixing = tongzhileixing;
	}
	
	public String getTongzhineirong() {
		return tongzhineirong;
	}

	public void setTongzhineirong(String tongzhineirong) {
		this.tongzhineirong = tongzhineirong;
	}
	
	public Integer getFasongzhuangtai() {
		return fasongzhuangtai;
	}

	public void setFasongzhuangtai(Integer fasongzhuangtai) {
		this.fasongzhuangtai = fasongzhuangtai;
	}
	
	public Date getFasongshijian() {
		return fasongshijian;
	}

	public void setFasongshijian(Date fasongshijian) {
		this.fasongshijian = fasongshijian;
	}
	
	public Integer getJieshouzhuangtai() {
		return jieshouzhuangtai;
	}

	public void setJieshouzhuangtai(Integer jieshouzhuangtai) {
		this.jieshouzhuangtai = jieshouzhuangtai;
	}
	
	public Date getJieshoushijian() {
		return jieshoushijian;
	}

	public void setJieshoushijian(Date jieshoushijian) {
		this.jieshoushijian = jieshoushijian;
	}
	
	public Integer getChongshicishu() {
		return chongshicishu;
	}

	public void setChongshicishu(Integer chongshicishu) {
		this.chongshicishu = chongshicishu;
	}
	
	public String getShibaiyuanyin() {
		return shibaiyuanyin;
	}

	public void setShibaiyuanyin(String shibaiyuanyin) {
		this.shibaiyuanyin = shibaiyuanyin;
	}
	
	public Date getJihuafasongshijian() {
		return jihuafasongshijian;
	}

	public void setJihuafasongshijian(Date jihuafasongshijian) {
		this.jihuafasongshijian = jihuafasongshijian;
	}

}
