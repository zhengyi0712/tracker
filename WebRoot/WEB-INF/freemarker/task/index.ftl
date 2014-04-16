<#import "*/common.ftl" as common />
<#import "task.common.ftl" as taskLib/>
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<@common.validateJs/>
		<@common.umJs/>
		<title>bug</title>
		<style type="text/css">
			.div-task{
				border-bottom:1px dashed gray;
				padding-bottom:5px;
				padding-top:10px;
			}
			.div-task .title{
				font-weight:bold;
				font-size:16px;
			}
		</style>
	</head>
	<body>
		<@common.topNavbar 'bug'/>
		<div class="container">
		<#if project??>
		<p>
			<big><strong>${project.name}</strong></big>&nbsp;
			<small class="text-danger">(${project.create_time})</small>
			<small class="text-muted">${project.intro!}</small>
		</p>
		
		<form class="form-inline" action="${ctx}/task/${project.id}">
			<div class="form-group">
				<input type="text" class="input-sm form-control" placeholder="标题/分派成员" maxlength="20"/>
			</div>
			<button class="btn btn-default btn-sm"><span class="glyphicon glyphicon-search"></span>&nbsp;搜索</button>
			<button type="button" class="btn btn-warning btn-sm" data-toggle="modal" data-target="#modal-add-task" data-remote="${ctx}/task/showCreateTask/${project.id}">
				<span class="glyphicon glyphicon-plus"></span>&nbsp;添加新的任务
			</button>
		</form>
		<#--添加新任务模态框start-->
		<div class="modal fade" role="dialog" id="modal-add-task">
			<div class="modal-dialog">
				<div class="modal-content"></div>
			</div>
		</div>
		<#--添加新任务模态框end-->
		<#--任务列表-->
			<#if list?? && list?size gt 0 >
				<#list list as task >
					<div class="div-task">
						<p>
							<a class="title">${task.title}</a>
							<small class="text-muted">(${task.create_time})</small>
						</p>
						<p>
							<@taskLib.tag task.tags />
							<span class="glyphicon glyphicon-user"></span>
							由<span class="text-info">${task.createUser.zh_name}</span>创建
						</p>		
					</div>
				</#list>
			<#else>
				<div class="alert alert-warning">无可显示数据</div>
			</#if>
		<#else>
		<div class="alert alert-warning">您尚未参与任何项目</div>		
		</#if>
		</div>
	</body>
</html>