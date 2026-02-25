-- 通知发送记录表
-- 用于记录就诊通知的发送状态、失败原因和重试机制

DROP TABLE IF EXISTS `tongzhijilu`;
CREATE TABLE `tongzhijilu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tongzhibianhao` varchar(200) DEFAULT NULL COMMENT '通知编号',
  `yuyuebianhao` varchar(200) DEFAULT NULL COMMENT '预约编号',
  `yishengzhanghao` varchar(200) DEFAULT NULL COMMENT '医生账号',
  `dianhua` varchar(200) DEFAULT NULL COMMENT '医生电话',
  `zhanghao` varchar(200) DEFAULT NULL COMMENT '用户账号',
  `shouji` varchar(200) DEFAULT NULL COMMENT '用户手机',
  `jiuzhenshijian` datetime DEFAULT NULL COMMENT '就诊时间',
  `tongzhineirong` text COMMENT '通知内容',
  `fasongqudao` varchar(50) DEFAULT NULL COMMENT '发送渠道(sms/站内信)',
  `fasongzhuangtai` varchar(20) DEFAULT '待发送' COMMENT '发送状态(待发送/成功/失败)',
  `shibaoyuanyin` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `chongshicishu` int(11) DEFAULT 0 COMMENT '重试次数',
  `zuidachongshi` int(11) DEFAULT 3 COMMENT '最大重试次数',
  `xiacichongshishijian` datetime DEFAULT NULL COMMENT '下次重试时间',
  `fasongshijian` datetime DEFAULT NULL COMMENT '实际发送时间',
  `beizhu` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_yuyuebianhao` (`yuyuebianhao`),
  KEY `idx_zhanghao` (`zhanghao`),
  KEY `idx_fasongzhuangtai` (`fasongzhuangtai`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知发送记录';

-- 添加用户表的接收渠道状态字段
ALTER TABLE `yonghu` ADD COLUMN `sms_enabled` tinyint(1) DEFAULT 1 COMMENT '短信接收开关(0关闭/1开启)';
ALTER TABLE `yonghu` ADD COLUMN `message_enabled` tinyint(1) DEFAULT 1 COMMENT '站内信接收开关(0关闭/1开启)';

-- 添加医生表的接收渠道状态字段
ALTER TABLE `yisheng` ADD COLUMN `sms_enabled` tinyint(1) DEFAULT 1 COMMENT '短信接收开关(0关闭/1开启)';
ALTER TABLE `yisheng` ADD COLUMN `message_enabled` tinyint(1) DEFAULT 1 COMMENT '站内信接收开关(0关闭/1开启)';
