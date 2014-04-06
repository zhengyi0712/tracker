<#import '*/common.ftl' as common/>
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<@common.validateJs />
		<title>个人中心</title>
		<script type="text/javascript" src="${ctx}/js/user-index.js"></script>
	</head>
	<body>
		<@common.topNavbar 'user'/>
		<div class="container">
			<h3 class="page-header">
				个人中心&gt;<span id="user-index-title">基本信息</span>
				<button class="btn btn-info pull-right" onclick="refresh()"><span class="glyphicon glyphicon-refresh"></span>&nbsp;刷新</button>
			</h3>
			<div class="row" id="row-body">
				<div class="col-md-2">
					<div class="list-group" id="user-menu">
						<a class="list-group-item active" href="${ctx}/user/userinfo">基本信息</a>		
						<a class="list-group-item">修改密码</a>
						<a class="list-group-item" href="${ctx}/project/myProjects">我的项目</a>		
						<#--管理员菜单-->
						<#if session.user.sysAdmin>
							<a class="list-group-item" data-dismiss="tooltip-menu" href="${ctx}/project/allProjects">所有项目</a>
							<a class="list-group-item" data-dismiss="tooltip-menu" href="${ctx}/user/allUsers">所有用户</a>
						</#if>
					</div>
				</div>
				<div class="col-md-10" id="col-content">
				</div>
			</div>
		</div>
	</body>
</html>