<?php

/*
 *  secret part
 */
 
$db_host = "localhost";
$db_user = "XXX";
$db_secret = "XXX";
$db_db = "XXX";

/*
 *  public part
 */

  
/*
 *  connect to database with host/user/secret/db
 */  

mysql_connect($db_host, $db_user, $db_secret) or die(mysql_error());

mysql_select_db($db_db) or die(mysql_error());

/*
 *  create tables
 */  

mysql_query("CREATE TABLE IF NOT EXISTS `allowed-tokens` ( `Token` text NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='tokens that are allowed for set/get of values'")
  or die(mysql_error());
  
mysql_query("CREATE TABLE IF NOT EXISTS `id-name-time-log` (`id` text NOT NULL, `name` text NOT NULL, `time` text NOT NULL, `log` text NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='log files'")
  or die(mysql_error());
  
mysql_query("CREATE TABLE IF NOT EXISTS `id-name-value` (`id` text NOT NULL, `name` text NOT NULL, `value` text NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='storage'")
  or die(mysql_error());
  
echo "databases created.";
