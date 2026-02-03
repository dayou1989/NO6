package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知记录
 * 用于记录通知发送状态和重试信息
 * @author 
 * @email 
 * @date 2025-03-27 15:44:15
 */
@TableName("tongzhijilu")
public class TongzhijiluEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public TongzhijiluEntity() {
	}

	/**
	 * 主键id
	 */
	@TableId
	private Long id;

	/**
	 * 预约编号
	 */
	private String yuyuebianhao;

	/**
	 * 用户账号
	 */
	private String zhanghao;

	/**
	 * 用户手机
	 */
	private String shouji;

	/**
	 * 通知类型（1-预约成功通知，2-就诊前24小时提醒，3-就诊前1小时提醒，4-就诊当天提醒）
	 */
	private Integer tongzhileixing;

	/**
	 * 通知内容
	 */
	private String tongzhineirong;

	/**
	 * 发送渠道（sms-短信，app-应用内，email-邮件）
	 */
	private String fasongqudao;

	/**
	 * 发送状态（0-待发送，1-发送成功，2-发送失败）
	 */
	private Integer fasongzhuangtai;

	/**
	 * 失败原因
	 */
	private String shibaiyuanyin;

	/**
	 * 重试次数
	 */
	private Integer chongshicishu;

	/**
	 * 最大重试次数
	 */
	private Integer zuidachongshicishu;

	/**
	 * 计划发送时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date jihuafasongshijian;

	/**
	 * 实际发送时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date shijifasongshijian;

	/**
	 * 创建时间
	 */
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getShouji() {
		return shouji;
	}

	public void setShouji(String shouji) {
		this.shouji = shouji;
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

	public String getFasongqudao() {
		return fasongqudao;
	}

	public void setFasongqudao(String fasongqudao) {
		this.fasongqudao = fasongqudao;
	}

	public Integer getFasongzhuangtai() {
		return fasongzhuangtai;
	}

	public void setFasongzhuangtai(Integer fasongzhuangtai) {
		this.fasongzhuangtai = fasongzhuangtai;
	}

	public String getShibaiyuanyin() {
		return shibaiyuanyin;
	}

	public void setShibaiyuanyin(String shibaiyuanyin) {
		this.shibaiyuanyin = shibaiyuanyin;
	}

	public Integer getChongshicishu() {
		return chongshicishu;
	}

	public void setChongshicishu(Integer chongshicishu) {
		this.chongshicishu = chongshicishu;
	}

	public Integer getZuidachongshicishu() {
		return zuidachongshicishu;
	}

	public void setZuidachongshicishu(Integer zuidachongshicishu) {
		this.zuidachongshicishu = zuidachongshicishu;
	}

	public Date getJihuafasongshijian() {
		return jihuafasongshijian;
	}

	public void setJihuafasongshijian(Date jihuafasongshijian) {
		this.jihuafasongshijian = jihuafasongshijian;
	}

	public Date getShijifasongshijian() {
		return shijifasongshijian;
	}

	public void setShijifasongshijian(Date shijifasongshijian) {
		this.shijifasongshijian = shijifasongshijian;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	/**
	 * 获取通知类型名称
	 */
	public String getTongzhileixingName() {
		switch (tongzhileixing) {
			case 1: return "预约成功通知";
			case 2: return "就诊前24小时提醒";
			case 3: return "就诊前1小时提醒";
			case 4: return "就诊当天提醒";
			default: return "未知类型";
		}
	}

	/**
	 * 获取发送状态名称
	 */
	public String getFasongzhuangtaiName() {
		switch (fasongzhuangtai) {
			case 0: return "待发送";
			case 1: return "发送成功";
			case 2: return "发送失败";
			default: return "未知状态";
		}
	}
}
