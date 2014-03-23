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
				
				</div>
			
			</div>
		</div>
	</body>
</html>