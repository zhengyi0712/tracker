<#import "*/common.ftl" as common>
<!DOCTYPE html>
<html>
<head>
	<@common.headerReference />
	<@common.umJs />
	<title>欢迎访问bugsfly</title>
</head>
<body>
	<@common.topNavbar/>
	<div class="container">
		<h1 class="page-header">UM测试</h1>
		<textarea style="width:100%;height:400px;" id="editor"></textarea>
	</div>
	<script>
		UM.getEditor("editor");
	</script>
</body>
</html>