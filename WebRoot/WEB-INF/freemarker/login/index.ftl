<#import "*/common.ftl" as common>
<!DOCTYPE html>
<html>
<head>
	<@common.headerReference />
	<title>欢迎访问bugsfly</title>
	<meta name="Keywords" content="bugsfly">
	<meta name="description" content="bugsfly - 简洁易用的bug跟踪系统">
	<style type="text/css">
	#loginBtn{
		width:150px;
	}
	@media (max-width:768px){
		#loginBtn{
		width:100%;
		}
	}
	</style>
</head>
<body>
	<div class="container-fluid" style="width:500px">
		<h2 class="page-header text-center">
			<img src="${ctx}/images/logo.jpg" width="50px" height="40px"/>
			登录bugs fly
		</h2>
		<form class="form-horizontal" action="/login/login" name="loginForm">
			<div class="form-group">
				<label class="col-md-3 control-label" for="account">帐号：</label>
				<div class="col-md-9">
					<input id="account" type="text" class="form-control" placeholder="输入手机号或者邮箱"/>	
				</div>
			</div>	
			<div class="form-group">
				<label class="col-md-3 control-label" for="pwd">密码：</label>
				<div class="col-md-9">
					<input id="pwd" type="password" class="form-control"/>
				</div>
			</div>
			<div class="form-group">
				<div class="col-md-9 col-md-offset-3">
						<button class="btn btn-primary" type="submit" id="loginBtn">登录</button>
				</div>
			</div>
		</form>
	</div>
</body>
</html>