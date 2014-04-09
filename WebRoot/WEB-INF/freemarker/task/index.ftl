<#import "*/common.ftl" as common />
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<title>bug</title>
	</head>
	<body>
		<@common.topNavbar 'bug'/>
		<div class="container">
		<#if project??>
		<p class="lead">${project.name}<small>(${project.create_time})</small></p>
		<p class="text-muted">${project.intro}</p>
		<form class="form-inline" action="${ctx}/task/${project.id}">
			<div class="">
		</form>
		
		<#else>
		<div class="alert alert-warning">您尚未参与任何项目</div>		
		</#if>
		</div>
	</body>
</html>