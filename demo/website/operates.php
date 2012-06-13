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


$id = filter_input(INPUT_POST,'id',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);

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

if ($is_allowed_token) {
	echo "okay";
}

?>

