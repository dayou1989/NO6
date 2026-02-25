package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.annotations.TableField;
import com.cl.entity.TongzhijiluEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

public class TongzhijiluView extends TongzhijiluEntity<TongzhijiluView> {
    private static final long serialVersionUID = 1L;

    public TongzhijiluView() {
    }

    public TongzhijiluView(TongzhijiluEntity<TongzhijiluEntity> entity) {
        try {
            BeanUtils.copyProperties(this, entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
