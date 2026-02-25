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
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String tongzhibianhao;
    
    private String yuyuebianhao;
    
    private String yishengzhanghao;
    
    private String dianhua;
    
    private String zhanghao;
    
    private String shouji;
    
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date jiuzhenshijian;
    
    private String tongzhineirong;
    
    private String fasongqudao;
    
    private String fasongzhuangtai;
    
    private String shibaoyuanyin;
    
    private Integer chongshicishu;
    
    private Integer zuidachongshi;
    
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date xiacichongshishijian;
    
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date fasongshijian;
    
    private String beizhu;

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
    public String getYishengzhanghao() {
        return yishengzhanghao;
    }
    public void setYishengzhanghao(String yishengzhanghao) {
        this.yishengzhanghao = yishengzhanghao;
    }
    public String getDianhua() {
        return dianhua;
    }
    public void setDianhua(String dianhua) {
        this.dianhua = dianhua;
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
    public Date getJiuzhenshijian() {
        return jiuzhenshijian;
    }
    public void setJiuzhenshijian(Date jiuzhenshijian) {
        this.jiuzhenshijian = jiuzhenshijian;
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
    public String getFasongzhuangtai() {
        return fasongzhuangtai;
    }
    public void setFasongzhuangtai(String fasongzhuangtai) {
        this.fasongzhuangtai = fasongzhuangtai;
    }
    public String getShibaoyuanyin() {
        return shibaoyuanyin;
    }
    public void setShibaoyuanyin(String shibaoyuanyin) {
        this.shibaoyuanyin = shibaoyuanyin;
    }
    public Integer getChongshicishu() {
        return chongshicishu;
    }
    public void setChongshicishu(Integer chongshicishu) {
        this.chongshicishu = chongshicishu;
    }
    public Integer getZuidachongshi() {
        return zuidachongshi;
    }
    public void setZuidachongshi(Integer zuidachongshi) {
        this.zuidachongshi = zuidachongshi;
    }
    public Date getXiacichongshishijian() {
        return xiacichongshishijian;
    }
    public void setXiacichongshishijian(Date xiacichongshishijian) {
        this.xiacichongshishijian = xiacichongshishijian;
    }
    public Date getFasongshijian() {
        return fasongshijian;
    }
    public void setFasongshijian(Date fasongshijian) {
        this.fasongshijian = fasongshijian;
    }
    public String getBeizhu() {
        return beizhu;
    }
    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }
}
