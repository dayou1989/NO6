-- H2测试数据库初始化脚本
-- 通知记录表
CREATE TABLE IF NOT EXISTS `tongzhijilu` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `addtime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `tongzhibianhao` VARCHAR(200),
  `yuyuebianhao` VARCHAR(200),
  `zhanghao` VARCHAR(200),
  `yishengzhanghao` VARCHAR(200),
  `tongzhileixing` INT,
  `tongzhineirong` LONGTEXT,
  `fasongzhuangtai` INT DEFAULT 0,
  `fasongshijian` DATETIME,
  `jieshouzhuangtai` INT DEFAULT 0,
  `jieshoushijian` DATETIME,
  `chongshicishu` INT DEFAULT 0,
  `shibaiyuanyin` VARCHAR(500),
  `jihuafasongshijian` DATETIME
);

-- 医生预约表
CREATE TABLE IF NOT EXISTS `yishengyuyue` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `addtime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `yuyuebianhao` VARCHAR(200) UNIQUE,
  `yishengzhanghao` VARCHAR(200),
  `dianhua` VARCHAR(200),
  `yuyueshijian` DATETIME,
  `zhanghao` VARCHAR(200),
  `shouji` VARCHAR(200),
  `yuyuebeizhu` VARCHAR(200),
  `sfsh` VARCHAR(200) DEFAULT '待审核',
  `shhf` LONGTEXT
);

-- 就诊通知表
CREATE TABLE IF NOT EXISTS `jiuzhentongzhi` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `addtime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `tongzhibianhao` VARCHAR(200) UNIQUE,
  `yishengzhanghao` VARCHAR(200),
  `dianhua` VARCHAR(200),
  `jiuzhenshijian` DATETIME,
  `tongzhishijian` DATETIME,
  `zhanghao` VARCHAR(200),
  `shouji` VARCHAR(200),
  `tongzhibeizhu` VARCHAR(200)
);

-- 系统日志表
CREATE TABLE IF NOT EXISTS `syslog` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `addtime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `username` VARCHAR(200) NOT NULL,
  `operation` VARCHAR(200) NOT NULL,
  `method` VARCHAR(200),
  `params` LONGTEXT,
  `time` BIGINT,
  `ip` VARCHAR(200)
);
