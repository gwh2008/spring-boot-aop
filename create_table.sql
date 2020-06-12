
CREATE TABLE `admin_log` (
  `operation` varchar(200) DEFAULT NULL ,
  `type` varchar(200) DEFAULT NULL,
  `remote_addr` varchar(200) DEFAULT NULL,
  `request_uri` varchar(200) DEFAULT NULL,
  `method` varchar(200) DEFAULT NULL,
  `params` varchar(200) DEFAULT NULL,
  `operate_date` datetime DEFAULT NULL,
  `user_id` varchar(200) DEFAULT NULL,
  `user_name` varchar(200) DEFAULT NULL,
  `result_params` varchar(200) DEFAULT NULL,
  `exception_log` varchar(200) DEFAULT NULL,
  `log_id` int(11) NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET= utf8mb4;