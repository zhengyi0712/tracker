<#import "*/common.ftl" as common>
<!DOCTYPE html>
<html>
	<head>
		<@common.headerReference />
		<title>500</title>
	</head>
	<body>
		<div class="jumbotron">
	        <h1>500 Internal Server Error</h1>
	         <#if message??>
	        <p>${message}</p>
	        <#else>
	        <p>服务器程序发生了错误，无法完成请求。您可以将此错误发生的原因详细说明，反馈给管理员。</p>
	        </#if>
	        <p><a  href="${ctx}" class="btn btn-primary"><span class="glyphicon glyphicon-home"></span> 回到主页 </a></p>
      	</div>
	</body>
</html>