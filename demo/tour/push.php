<?php

include 'settings.php';


/*
 *  public part
 */


$id = filter_input(INPUT_POST,'id',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
$varname = filter_input(INPUT_POST,'varname',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);;
$value = filter_input(INPUT_POST,'value',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);

/*
 *  filter out id string unwanted chars
 */
 
$id = preg_replace("/[^0-9a-zA-Z\-+]/i", '', $id);
  
/*
 *  connect to database with host/user/secret/db
 */  

mysql_connect($db_host, $db_user, $db_secret) or die(mysql_error());

mysql_select_db($db_db) or die(mysql_error());


$is_allowed_query = mysql_query("SELECT * FROM `allowed-tokens` WHERE `Token`='" . $id . "'")
or die(mysql_error());  



$is_allowed_token = mysql_fetch_assoc($is_allowed_query) ? true : false;

$is_allowed_token or die("id unknown");

/*
 *  encode name and value
 */
 
$varname = urlencode($varname);
$value = urlencode($value);


mysql_query("DELETE FROM `id-name-value` WHERE (`id`='" . $id . "' AND `name`='" . $varname . "')")
  or die(mysql_error());  
  
mysql_query("INSERT INTO `id-name-value` VALUES('" . $id . "','" . $varname . "','" . $value . "')")
  or die(mysql_error());  

/*
 * reflect current change
 */

echo urldecode($value);

?>

