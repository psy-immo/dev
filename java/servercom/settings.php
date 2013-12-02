<?php

/**
 *  secret part
 */
 
$db_host = "localhost";
$db_user = "servercom";
$db_secret = "testing";
$db_db = $db_user;

/**
 * set logging
 */

ini_set("log_errors", 1);
ini_set("error_log", "/tmp/servercom-error.log");

$src = "unspecified";

/**
 * set debug level
 */

function debug_log($x) {
	error_log($GLOBALS["src"].":".$x);
};

function debug_die($x) {
	error_log($GLOBALS["src"].": DIE " . $x);
	die("Error " . $x);
};

/**
 * check the given id
 */
	
function check_id($x) {
	$is_allowed_query = mysql_query("SELECT * FROM `allowed-tokens` WHERE `Token`='" . $x . "'")
	or debug_die("Check-Id: ".mysql_error());
	
	return mysql_fetch_assoc($is_allowed_query) ? true : false;
};