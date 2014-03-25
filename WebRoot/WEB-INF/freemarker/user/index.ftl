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
					<dl class="dl-horizontal">
						<dt>姓名：</dt><dd>${session.user.ch_name}</dd>
						<#if session.user.en_name??>
						<dt>英文名：</dt><dd>${session.user.en_name}</dd>
						</#if>
						<#if session.user.email??>
						<dt>邮箱：</dt><dd>${session.user.email}</dd>
						</#if>
						<#if session.user.mobile??>
						<dt>手机：</dt><dd>${session.user.mobile}</dd>
						</#if>
						<dt>开户时间 ：</dt><dd>${session.user.create_time}</dd>
						<#if session.user.login_time??>
						<dt>上次登录 ：</dt><dd>${session.user.login_time}</dd>
						</#if>
					</dl>			
				</div>
			</div>
		</div>
	</body>
</html>