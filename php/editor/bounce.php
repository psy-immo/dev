<?php

/**
 * make chromium happy again
 * 
 */
 
 header("X-XSS-Protection: 0",false);

/**
 * bounce
 * 
 */ 
 

$doc = filter_input(INPUT_POST,'doc',FILTER_SANITIZE_STRING,!FILTER_FLAG_STRIP_LOW);


echo html_entity_decode($doc);

?>

