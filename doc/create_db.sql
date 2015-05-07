
/**建立数据库*/
CREATE DATABASE `photography` DEFAULT CHARACTER SET utf8;

use `photography`;

/**用户表 */
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL PRIMARY KEY,
  `login_name` varchar(40) comment '登录名称',
  `password` varchar(255) comment '密码',
  `type` varchar(2) comment '用户类型',
  `nack_name` varchar(40) comment '昵称',
  `real_name` varchar(40) comment '真实姓名',
  `sex` varchar(2) comment '性别',
  `mobile` varchar(20) comment '手机号码',
  `email` varchar(20) comment '邮箱',
  `birth_day` date comment '生日',
  `verify` varchar(2) comment '是否审核',
  `enable` varchar(2) comment '是否激活',
  `province` varchar(20) comment '省',
  `city` varchar(20) comment '市',
  `county` varchar(20) comment '区',
  `id_card` varchar(20) comment '身份证号',
  `company_name` varchar(20) comment '单位名称',
  `comfirm_pic` varchar(300) comment '验证照片地址',
  `head_pic` varchar(300) comment '头像照片地址',
  `qq_number` varchar(40) comment 'QQ',
  `person_signature` varchar(300) comment '个性签名',
  `create_time` datetime comment '注册时间',
  `last_update_time` datetime comment '最后登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**活动表 */
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` varchar(36) NOT NULL PRIMARY KEY,
  `name` varchar(200) comment '活动名称',
  `type` varchar(10) comment '活动类型',
  `start_time` datetime comment '活动开始时间',
  `time_length` varchar(50) comment '活动时长',
  `people_num` varchar(50) comment '活动人数',
  `less_num` varchar(50) comment '最低人数',
  `model_num` varchar(50) comment '模特数量',
  `place` varchar(200) comment '活动地点',
  `venue_place` varchar(200) comment '集合地点',
  `province` varchar(20) comment '省',
  `city` varchar(20) comment '市',
  `county` varchar(20) comment '区',
  `contact` varchar(200) comment '联系方式',
  `des` varchar(1000) comment '活动介绍',
  `fee_des` varchar(1000) comment '费用介绍',
  `xc_des` varchar(1000) comment '行程介绍',
  `cost` varchar(300) comment '活动费用',
  `photos` varchar(500) comment '活动照片',
  `model_photos` varchar(500) comment '场地、模特 照片',
  `create_user` varchar(36) comment '创建用户',
  `create_time` datetime comment '创建时间',
  `last_update_time` datetime comment '最后修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/**文件组 */
DROP TABLE IF EXISTS `file_group`;
CREATE TABLE `file_group` (
  `id` varchar(36) NOT NULL PRIMARY KEY,
  `create_time` datetime comment '注册时间',
  `last_update_time` datetime comment '最后登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/**文件表 */
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
  `id` varchar(36) NOT NULL PRIMARY KEY,
  `real_name` varchar(200) comment '真实文件名',
  `real_path` varchar(500) comment '存储文件全路径',
  `ext` varchar(20) comment '活动开始时间',
  `file_group` varchar(36) comment '活动时长',
  `create_time` datetime comment '创建时间',
  `last_update_time` datetime comment '最后修改时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
