<html><body>

Push value:
<br/>
<form action="push.php" method="post"> 
id: <input name="id" type="text" /> 
name: <input name="varname" type="text" /> 
value: <input name="value" type="text" /> 
<input type="submit" />
</form>
<br/>

Pull value:
<br/>
<form action="pull.php" method="post"> 
id: <input name="id" type="text" /> 
name: <input name="varname" type="text" /> 
<input type="submit" />
</form>
<br/>

Pull values:
<br/>
<form action="pullall.php" method="post"> 
id: <input name="id" type="text" /> 
name prefix: <input name="varname" type="text" /> 
<input type="submit" />
</form>
<br/>

Log string:
<br/>
<form action="log.php" method="post"> 
id: <input name="id" type="text" /> 
name: <input name="varname" type="text" /> 
value: <input name="value" type="text" /> 
<input type="submit" />
</form>
<br/>

Add id:
<form action="add.php" method="post"> 
id: <input name="id" type="text" /> 
secret: <input name="secret" type="text" /> 
<input type="submit" />
</form>

</body></html>
