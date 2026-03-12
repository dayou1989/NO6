-- 为就诊通知表添加新字段，支持通知状态和重试机制
ALTER TABLE `jiuzhentongzhi` 
ADD COLUMN `status` VARCHAR(50) DEFAULT '待发送' COMMENT '通知状态：待发送、发送中、发送成功、发送失败' AFTER `tongzhibeizhu`,
ADD COLUMN `retry_count` INT DEFAULT 0 COMMENT '重试次数' AFTER `status`,
ADD COLUMN `max_retry_count` INT DEFAULT 3 COMMENT '最大重试次数' AFTER `retry_count`,
ADD COLUMN `next_retry_time` DATETIME DEFAULT NULL COMMENT '下次重试时间' AFTER `max_retry_count`,
ADD COLUMN `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '发送失败原因' AFTER `next_retry_time`,
ADD COLUMN `notice_type` VARCHAR(100) DEFAULT NULL COMMENT '通知类型：预约成功提醒、就诊前提醒等' AFTER `fail_reason`;

-- 添加索引以提高查询效率
CREATE INDEX idx_status ON jiuzhentongzhi(status);
CREATE INDEX idx_next_retry_time ON jiuzhentongzhi(next_retry_time);
