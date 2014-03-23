<#import '*/common.ftl' as common/>
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<title>个人中心</title>
		<style type="text/css">
			.btn-menu{
				margin:10px;
			}
		</style>
	</head>
	<body>
		<@common.topNavbar 'user'/>
		<div class="container">
			<h3 class="page-header">个人中心—系统管理</h3>
			<div class="row">
				<div class="col-md-2">
					<@common.personalCenterMenu 'admin_menu'/>
				</div>
				<div class="col-md-10">
						<a class="btn btn-info btn-menu pull-left" style="width:200px">所有公司</a>
						<a class="btn btn-info btn-menu pull-left" style="width:200px">所有用户</a>
						<div class="clearfix"></div>
						<a class="btn btn-info btn-menu pull-left" style="width:200px">所有公司</a>
						<a class="btn btn-info btn-menu pull-left" style="width:200px">所有公司</a>
				</div>
			
			</div>
		</div>
	</body>
</html>