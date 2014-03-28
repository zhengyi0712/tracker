<#import '*/common.ftl' as common/>
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<title>个人中心</title>
		<script type="text/javascript" src="${ctx}/js/user-index.js"></script>
	</head>
	<body>
		<@common.topNavbar 'user'/>
		<div class="container">
			<h3 class="page-header">个人中心&gt;<span id="user-index-title">基本信息</span></h3>
			<div class="row" id="row-body">
				<div class="col-md-2">
					<div class="list-group" id="user-menu">
						<a class="list-group-item active" href="${ctx}/user/userinfo">基本信息</a>		
						<a class="list-group-item" href="${ctx}/team/myTeams">所在团队</a>		
						<a class="list-group-item" href="${ctx}/project/myProjects">参与项目</a>		
						<#if session.user.isAdmin>
						<a class="list-group-item visible-xs" data-toggle="tooltip-menu" data-container="#row-body" data-placement="bottom" href="#adminMenu">系统管理</a>
						<a class="list-group-item hidden-xs" data-toggle="tooltip-menu" data-container="#row-body" data-placement="right" href="#adminMenu">系统管理</a>
						</#if>
					</div>
					<#--管理员菜单-->
					<div class="tooltip-menu" id="adminMenu">
						<a class="list-group-item" data-dismiss="tooltip-menu" href="${ctx}/team/allTeams">所有团队</a>
						<a class="list-group-item" data-dismiss="tooltip-menu" href="${ctx}/project/allProjects">所有项目</a>
						<a class="list-group-item" data-dismiss="tooltip-menu" href="${ctx}/user/allUsers">所有用户</a>
					</div>
				</div>
				<div class="col-md-10" id="col-content">
				</div>
			</div>
		</div>
	</body>
</html>