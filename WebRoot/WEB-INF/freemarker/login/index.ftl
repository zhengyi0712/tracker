<#import "*/common.ftl" as common>
<!DOCTYPE html>
<html>
<head>
	<@common.preventIE />
	<@common.headerReference />
	<@common.validateJs />
	<title>欢迎访问bugsfly</title>
	<meta name="Keywords" content="bugsfly">
	<meta name="description" content="bugsfly - 简洁易用的bug跟踪系统，JFinal项目">
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
		<#--错误警告框-->
		<#if msg?? >
			<div class="alert alert-warning alert-dismissable text-center">
				<button type="button" class="close" data-dismiss="alert" aria-hidden="true">x</button>
				${msg}
			</div>
		</#if>
		<form class="form-horizontal" action="${ctx}/login/login" name="loginForm" method="post">
			<div class="form-group">
				<label class="col-md-3 control-label" for="account">帐号：</label>
				<div class="col-md-9">
					<input id="account" name="account" type="text" class="form-control" placeholder="输入手机号或者邮箱"/>	
				</div>
			</div>	
			<div class="form-group">
				<label class="col-md-3 control-label" for="pwd">密码：</label>
				<div class="col-md-9">
					<input id="pwd" type="password" class="form-control" name="pwd" maxlength="16"/>
				</div>
			</div>
			<div class="form-group">
				<div class="col-md-9 col-md-offset-3">
						<button class="btn btn-primary" type="submit" id="loginBtn">登录</button>
				</div>
			</div>
		</form>
	</div>
	<script type="text/javascript">
		$(document.loginForm).validate({
			rules:{
				account:{
					required:true,
					account:true
				}
			}
		});
	</script>
</body>
</html>