<#import '*/common.ftl' as common/>
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<title>所有公司</title>
	</head>
	<body>
		<@common.topNavbar 'user'/>
		<div class="container">
			<h3 class="page-header">个人中心—基本信息</h3>
			<div class="row">
				<div class="col-md-2">
					<@common.personalCenterMenu 'admin_menu'/>
				</div>
				<div class="col-md-10">
					<form class="form-inline" role="form">
					<div class="form-group">
						<input type="text" placeholder="团队名称" class="form-control">
					</div>
					<a class="btn btn-default">
						<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
					</a>
					<a class="btn btn-primary">
						<span class="glyphicon glyphicon-plus"></span>&nbsp;创建团队
					</a>
					</form>
					<#if !page.list?? || page.list?size == 0 >
						<div class="alert alert-warning">
							无可显示数据
						</div>
					<#else>
					
					
					</#if>
					
				</div>
			</div>
		</div>
	</body>
</html>