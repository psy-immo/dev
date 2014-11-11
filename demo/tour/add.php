<?php

include 'settings.php';

/*
 *  public part
 */


$id = filter_input(INPUT_POST,'id',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
$secret = filter_input(INPUT_POST,'secret',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);

if ($setup_secret != $secret) die("Tell me the secret!");

/*
 *  filter out id string unwanted chars
 */
 
$id = preg_replace("/[^0-9a-zA-Z\-+]/i", '', $id);
  
/*
 *  connect to database with host/user/secret/db
 */  

mysql_connect($db_host, $db_user, $db_secret) or die(mysql_error());

mysql_select_db($db_db) or die(mysql_error());

mysql_query("INSERT INTO `allowed-tokens` VALUES('" . $id . "')") or die(mysql_error());


?>

