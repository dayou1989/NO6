-- 通知记录表结构
-- 用于记录所有通知发送的详细信息，支持失败重试机制

DROP TABLE IF EXISTS `tongzhi_record`;
CREATE TABLE `tongzhi_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tongzhiId` bigint(20) DEFAULT NULL COMMENT '关联的通知ID',
  `channelType` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知渠道类型(SMS/EMAIL/SYSTEM)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '状态(PENDING/SUCCESS/FAILED)',
  `retryCount` int(11) DEFAULT '0' COMMENT '重试次数',
  `maxRetryCount` int(11) DEFAULT '3' COMMENT '最大重试次数',
  `lastRetryTime` timestamp NULL DEFAULT NULL COMMENT '最后重试时间',
  `errorMessage` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '错误信息',
  `receiver` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者(手机号/邮箱/账号)',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updatetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_tongzhi_id` (`tongzhiId`),
  KEY `idx_retry_count` (`retryCount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知发送记录表';
