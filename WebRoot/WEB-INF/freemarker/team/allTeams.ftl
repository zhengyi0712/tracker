<#import '*/common.ftl' as common/>
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<title>所有团队</title>
	</head>
	<body>
		<@common.topNavbar 'user'/>
		<div class="container">
			<h3 class="page-header">个人中心>系统管理>所有团队</h3>
			<div class="row">
				<div class="col-md-2">
					<@common.personalCenterMenu 'admin_menu'/>
				</div>
				<div class="col-md-10">
					<form class="form-inline" role="form" action="${ctx}/team/allTeams">
					<div class="form-group">
						<input type="text" placeholder="团队名称" class="form-control" name="name" value="${name!}">
					</div>
					<button class="btn btn-default" type="submit">
						<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
					</button>
					<a class="btn btn-primary" href="${ctx}/team/createTeam" data-toggle="modal" data-target="#div-create-team">
						<span class="glyphicon glyphicon-plus"></span>&nbsp;创建团队
					</a>
					</form>
					<#--创建团队模态框-->
					<div class="modal fade" id="div-create-team" role="dialog" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content"></div>
						</div>
					</div>
					<#if !page.list?? || page.list?size == 0 >
						<div class="alert alert-warning">
							无可显示数据
						</div>
					<#else>
						<table class="table table-responsive">
						<thead>
							<tr>
								<th>序号</th>
								<th>团队名称</th>
								<th>创建时间</th>
								<th>项目数</th>
								<th>人数</th>
								<th>操作</th>
							</tr>
						</thead>
						<tbody>				
						<#list page.list as team>
							<tr>
								<td>${team_index+1}</td>							
								<td>${team.name}</td>							
								<td>${team.create_time}</td>							
								<td>${team.p_count!0}</td>							
								<td>${team.u_count!0}</td>
								<td><a>查看项目</a>丨<a>查看成员</a></td>						
							</tr>
						</#list>
						</tbody>
						</table>
						${pageLink!}
					</#if>
					
				</div>
			</div>
		</div>
	</body>
</html>