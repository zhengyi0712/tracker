<#import "*/common.ftl" as common>
<!DOCTYPE html>
<html>
	<head>
		<@common.headerReference />
		<title>401</title>
	</head>
	<body>
		<div class="jumbotron">
	        <h1>401 Unauthorized</h1>
	         <#if message??>
	        <p>${message}</p>
	        <#else>
	        <p>很抱歉，身份验证没有通过，请确认。</p>
	        </#if>
	        <p><a  href="${ctx}" class="btn btn-primary"><span class="glyphicon glyphicon-home"></span> 回到主页 </a></p>
      	</div>
	</body>
</html>