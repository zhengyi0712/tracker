<#import '*/common.ftl' as common/>
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<title>个人中心</title>
	</head>
	<body>
		<@common.topNavbar 'user'/>
		<div class="container">
			<h3 class="page-header">个人中心—基本信息</h3>
			<div class="row">
				<div class="col-md-2">
					<@common.personalCenterMenu 'basic_info'/>
				</div>
				<div class="col-md-10">
					<button class="btn btn-primary" type="button" data-toggle="tooltip-menu" data-target="#tooltipMenu">工具提示菜单演示</button>
					<div class="tooltip-menu" id="tooltipMenu">
						<a class="list-group-item" href="#">菜单一二三四五</a>
						<a class="list-group-item" href="#">菜单一二三四五</a>
						<a class="list-group-item" href="#">菜单一二三四五</a>
						<a class="list-group-item" href="#">菜单一二三四五</a>
						<a class="list-group-item" href="#">菜单一二三四五</a>
						<a class="list-group-item" href="#">菜单一二三四五</a>
					</div>
				</div>
			
			</div>
		</div>
	</body>
</html>