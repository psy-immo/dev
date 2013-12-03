<?php

include 'settings.php';

$src = "setupdb.php";

/**
 * server communications script
 */

/**
 *  connect to database with host/user/secret/db
*/

mysql_connect($db_host, $db_user, $db_secret) or debug_die("MySQL-Connect: ".mysql_error());

mysql_set_charset("utf8") or debug_die("MySQL-Set-UTF8: ".mysql_error());

mysql_select_db($db_db) or debug_die("MySQL-Select-Db: ".mysql_error());

/**
 * create tables
 */

mysql_query("CREATE TABLE IF NOT EXISTS `allowed-tokens` ( `Token` text NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='tokens that are allowed for set/get of values'")
or debug_die("Create allowed-tokens: ".mysql_error());

mysql_query("CREATE TABLE IF NOT EXISTS `logs` (`id` text NOT NULL, `doc` text NOT NULL, `time` text NOT NULL, `log` text NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='log files'") 
or debug_die("Create logs: ".mysql_error());

mysql_query("CREATE TABLE IF NOT EXISTS `storage` (`id` text NOT NULL, `doc` text NOT NULL, `name` text NOT NULL, `value` text NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='log files'")
or debug_die("Create storage: ".mysql_error());

echo "Setup completed.";