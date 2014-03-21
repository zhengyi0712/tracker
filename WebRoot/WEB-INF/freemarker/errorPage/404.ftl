<#import "*/common.ftl" as common>
<!DOCTYPE html>
<html>
	<head>
		<@common.headerReference />
		<title>404</title>
	</head>
	<body>
		<div class="jumbotron">
	        <h1>404 Not Found</h1>
	         <#if message??>
	        <p>${message}</p>
	        <#else>
	        <p>很遗憾，没有找到您要访问的页面。</p>
	        </#if>
	        <p><a  href="${ctx}" class="btn btn-primary"><span class="glyphicon glyphicon-home"></span> 回到主页 </a></p>
      	</div>
	</body>
</html>