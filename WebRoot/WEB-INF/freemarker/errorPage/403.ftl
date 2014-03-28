<#import "*/common.ftl" as common>
<!DOCTYPE html>
<html>
	<head>
		<@common.headerReference />
		<title>403</title>
	</head>
	<body>
		<div class="jumbotron">
	        <h1>403 Forbidden</h1>
	        <#if message??>
	        <p>${message}</p>
	        <#else>
	        <p>抱歉，操作被禁止。</p>
	        </#if>
      	</div>
	</body>
</html>