<#import "*/common.ftl" as common>
<!DOCTYPE html>
<html>
<head>
	<@common.headerReference />
	<link href="${ctx}/UM/themes/default/css/umeditor.min.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="${ctx}/UM/umeditor.config.js"></script>
	<script type="text/javascript" src="${ctx}/UM/umeditor.min.js"></script>
	<title>欢迎访问bugsfly</title>
</head>
<body>
	<div class="container">
		<h1 class="page-header">UM测试</h1>
		<textarea style="width:100%;height:400px;" id="editor"></textarea>
	</div>
	<script>
		showAlert("欢迎访问BugsFly");
		UM.getEditor("editor");
	</script>
</body>
</html>