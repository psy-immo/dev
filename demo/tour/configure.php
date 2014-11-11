<?php

include 'settings.php';

$secret = $_POST["setup_secret"];

if ($secret != $setup_secret) {
	echo "Wrong password!";
} else {

if ($new_setup_secret=='')
   $new_setup_secret = $setup_secret;

$settings = '<?php
/*
 *  secret part
 */
  
$db_host = "'. $_POST["db_host"] .'";
$db_user = "'. $_POST["db_user"] .'";
$db_secret = "'. $_POST["db_secret"] .'";
$db_db = "'. $_POST["db_db"] .'";

$setup_secret = "'. $_POST["new_setup_secret"] .'";
?>


';

$fp = fopen("settings.php", "w");

$fp or die('could not open settings.php for writing!');

fwrite($fp, $settings);

fclose($fp);

echo "Changes accepted. Next, <a href=\"setup-db.php\">setup MySQL database</a>.";
}
