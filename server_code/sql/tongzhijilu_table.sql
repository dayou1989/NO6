-- 通知记录表
DROP TABLE IF EXISTS `tongzhijilu`;
CREATE TABLE `tongzhijilu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tongzhibianhao` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知编号',
  `yuyuebianhao` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预约编号',
  `zhanghao` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户账号',
  `yishengzhanghao` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生账号',
  `tongzhileixing` int(11) DEFAULT NULL COMMENT '通知类型（1-预约成功通知，2-就诊前24小时提醒，3-就诊前1小时提醒，4-就诊时间通知）',
  `tongzhineirong` longtext COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `fasongzhuangtai` int(11) DEFAULT '0' COMMENT '发送状态（0-待发送，1-发送成功，2-发送失败，3-已取消）',
  `fasongshijian` datetime DEFAULT NULL COMMENT '发送时间',
  `jieshouzhuangtai` int(11) DEFAULT '0' COMMENT '接收状态（0-未接收，1-已接收）',
  `jieshoushijian` datetime DEFAULT NULL COMMENT '接收时间',
  `chongshicishu` int(11) DEFAULT '0' COMMENT '重试次数',
  `shibaiyuanyin` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失败原因',
  `jihuafasongshijian` datetime DEFAULT NULL COMMENT '计划发送时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录';
