<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Efml Web Compiler Module</title>
</head>
<?php
include 'settings.php';
?>
<body>
   <H1>Setup</H1>
   <form action="configure.php" method="post">
   Current setup secret: <input type="password" name="setup_secret"><br>
   New setup secret: <input type="password" name="new_setup_secret"><br>
	Database host: <input type="text" name="db_host" value="<?php echo $db_host; ?>"><br>
	Database user: <input type="text" name="db_user" value="<?php echo $db_user; ?>"><br>
	Database name: <input type="text" name="db_db" value="<?php echo $db_db; ?>"><br>
   Database secret: <input type="password" name="db_secret"><br>
<input type="submit" value="Setup!">
</form>
</body>
</html>
