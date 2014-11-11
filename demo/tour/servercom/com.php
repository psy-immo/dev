<?php

/**
 * load settings
 */


include 'settings.php';

$src = "com.php";

/**
 *  connect to database with host/user/secret/db
 */

mysql_connect($db_host, $db_user, $db_secret) or debug_die("MySQL-Connect: ".mysql_error());

mysql_set_charset("utf8") or debug_die("MySQL-Set-UTF8: ".mysql_error());

mysql_select_db($db_db) or debug_die("MySQL-Select-Db: ".mysql_error());

/**
 * get data
 */


$id = filter_input(INPUT_POST,'id',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
$q = filter_input(INPUT_POST,'q',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);

$id = preg_replace("/[^0-9a-zA-Z\-+]/i", '', $id);

debug_log("id: ".$id);
debug_log("q: ".$q);


if ($q == "test") {
	/**
	 * test whether the id is allowed
	 */
	if (check_id($id)) {
		echo "good";
	} else {
		echo "bad";
	}
} else {
	check_id($id) or debug_die("Id ".$id." access not granted.");

	if ($q == "log") {
		$timestamp = date('Y-m-d G:i:s T');

		$d = filter_input(INPUT_POST,'d',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
		$x = filter_input(INPUT_POST,'x',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);

		debug_log("d: ".$d);
		debug_log("x: ".$x);

		mysql_query("INSERT INTO `logs` VALUES('" . $id . "','" . $d . "','" . $timestamp ."','" . $x . "')")
		or debug_die("Insert into logs: ".mysql_error());
	} else if ($q == "save") {
		$d = filter_input(INPUT_POST,'d',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
		$x = filter_input(INPUT_POST,'x',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
		$y = filter_input(INPUT_POST,'y',FILTER_UNSAFE_RAW,0);
		
		debug_log("d: ".$d);
		debug_log("x: ".$x);
		debug_log("y: ".$y);
		
		$escaped_y = mysql_real_escape_string($y);
		
		debug_log("escaped_y: ".$escaped_y);
		
		mysql_query("DELETE FROM `storage` WHERE (`id`='" . $id . "' AND `doc`='" . $d . "' AND `name`='" . $x . "')")
		or debug_die("Delete from storage: ".mysql_error());
		
		mysql_query("INSERT INTO `storage` VALUES('" . $id . "','" . $d . "','" . $x . "','" . $escaped_y . "')")
		or debug_die("Insert into storage: ".mysql_error());
		
	} else if ($q == "avail") {
		$d = filter_input(INPUT_POST,'d',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
		$x = filter_input(INPUT_POST,'x',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
		
		
		debug_log("d: ".$d);
		debug_log("x: ".$x);
		
			
		$is_available_query = mysql_query("SELECT * FROM `storage` WHERE (`id`='" . $id . "' AND `doc`='" . $d . "' AND `name`='" . $x . "')")
		or debug_die("Select from storage: ".mysql_error());
		
		if (mysql_fetch_assoc($is_available_query)) {
			echo "yes";
		} else {
			echo "no";
		}	
	} else if ($q == "load") {
		$d = filter_input(INPUT_POST,'d',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
		$x = filter_input(INPUT_POST,'x',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);
		
		
		debug_log("d: ".$d);
		debug_log("x: ".$x);
		
			
		$is_available_query = mysql_query("SELECT * FROM `storage` WHERE (`id`='" . $id . "' AND `doc`='" . $d . "' AND `name`='" . $x . "')")
		or debug_die("Select from storage: ".mysql_error());
		
		$value = mysql_fetch_assoc($is_available_query);
		
		if ($value) {
			echo $value['value'];
		} 
	} else if ($q == "purge") {
		
		safeguard_die();
		
		mysql_query("DELETE FROM `storage` WHERE `id`='" . $id . "'")
		or debug_die("Purge from storage: ".mysql_error());
	}
	
}


