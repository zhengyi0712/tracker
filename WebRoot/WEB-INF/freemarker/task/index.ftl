<#import "*/common.ftl" as common />
<#import "task.common.ftl" as taskLib/>
<!DOCTYPE xhtml>
<html>
	<head>
		<@common.headerReference />
		<@common.validateJs/>
		<@common.umJs/>
		<title>查看任务-Bugs Fly</title>
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
			div.checkbox,div.radio{
				display:block !important;
				margin-left: 10px;
				padding: 2px 0px;
			}
			.edui-container,.edui-body-container{
				width:100% !important;
				min-height:300px !important;
			}
			.edui-scale{
				box-sizing:content-box;	
			}
			body{
				padding-bottom:20px;
			}
			.btn-sm{
				border-radius: 3px !important;
			}
		</style>
		<script type="text/javascript">
			$(function(){
				$("#updata-task-modal").bind("hidden.bs.modal",function(){
					$(this).removeData("bs.modal").find(".modal-content").empty();
				});
				$("#view-task-modal").bind("hidden.bs.modal",function(){
					$(this).removeData("bs.modal").find(".modal-content").empty();
				});
				$("#view-task-log-modal").bind("hidden.bs.modal",function(){
					$(this).removeData("bs.modal").find(".modal-content").empty();
				});
				//对下拉的多选与单选禁止点击事件传播，从而阻止点击后下拉菜单关闭
				$(".dropdown-menu .checkbox").click(function(e){
					e.stopPropagation();
				});
				$(".dropdown-menu .radio").click(function(e){
					e.stopPropagation();
				});
			});
		</script>
	</head>
	<body>
		<@common.topNavbar 'bug'/>
		<div class="container">
		<#if project??>
		<#--项目信息-->
		<p>
			<big><strong>${project.name}</strong></big>&nbsp;
			<small class="text-danger">(${project.create_time})</small>
			<small class="text-muted">${project.intro!}</small>
		</p>
		<#--搜索查询表单-->
		<form class="form-inline" action="${ctx}/task/${project.id}">
			<input type="text" name="title" id="form-search-ipt-title" class="form-control input-sm" maxlength="20" placeholder="任务标题" value="${title!}"/>
			<div class="btn-group">
				<button type="button" data-toggle="dropdown" class="btn btn-default btn-sm">标签选择<span class="caret"></span></button>
				<div class="dropdown-menu">
					<#list tags as tag>
					<div class="checkbox">	
						<label>
							<input type="checkbox" name="tagId" value="${tag.id}" <#if tagIdArr?? && tagIdArr?seq_contains(tag.id)>checked="checked"</#if> >&nbsp;${tag.name}
						</label>
					</div>
					</#list>
				</div>
			</div>
			<div class="btn-group">
				<button type="button" data-toggle="dropdown" class="btn btn-default btn-sm">状态选择<span class="caret"></span></button>
				<div class="dropdown-menu">
					<div class="checkbox">
						<label>
							<input type="checkbox" name="status" value="CREATED" <#if statusArr?? && statusArr?seq_contains('CREATED')>checked="checked"</#if> />&nbsp;新建						
						</label>
					</div>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="status" value="ASSIGNED" <#if statusArr?? && statusArr?seq_contains('ASSIGNED')>checked="checked"</#if> />&nbsp;已分派					
						</label>
					</div>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="status" value="FINISHED" <#if statusArr?? && statusArr?seq_contains('FINISHED')>checked="checked"</#if> />&nbsp;已完成						
						</label>
					</div>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="status" value="REWORKED" <#if statusArr?? && statusArr?seq_contains('REWORKED')>checked="checked"</#if> />&nbsp;已返工						
						</label>
					</div>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="status" value="CLOSED" <#if statusArr?? && statusArr?seq_contains('CLOSED')>checked="checked"</#if> />&nbsp;已关闭				
						</label>
					</div>
				</div>
			</div>
			<#--必须要项目多于一个人才有这个选项，因为系统管理员可以在项目没有人的情况下来查看-->
			<#if project.users?? && project.users?size gt 0 >
			<div class="btn-group">
				<button type="button" data-toggle="dropdown" class="btn btn-default btn-sm">任务接收人<span class="caret"></span></button>
				<div class="dropdown-menu">
				<#list project.users as u >
					<div class="checkbox">
						<label>
							<input type="checkbox" name="assignUserId" value="${u.id}" <#if assignUserIdArr?? && assignUserIdArr?seq_contains('${u.id}')>checked="checked"</#if> />&nbsp;${u.zh_name}&nbsp;${u.en_name!}
						</label>
					</div>
				</#list>			
				</div>
			</div>
			</#if>
			<button class="btn btn-primary btn-sm" type="submit"><span class="glyphicon glyphicon-search"></span>&nbsp;搜索</button>
			<#--添加任务，只有这个项目的测试和管理员才可以-->
			<#if 'ADMIN' == role! || 'TESTER' == role! >
			<button type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#add-task-modal" data-remote="${ctx}/task/showCreateTask/${project.id}">
				<span class="glyphicon glyphicon-plus"></span>&nbsp;添加任务
			</button>
			</#if>
			<#--切换项目，必须用户有多于一个项目才能切换-->
			<#if session.user.projects?? && session.user.projects?size gt 1 >
			<div class="btn-group">
				<button type="button" class="btn btn-default btn-sm" data-toggle="dropdown">切换项目<span class="caret"></button>
				<ul class="dropdown-menu" role="menu">
					<#list session.user.projects as p>
					<li><a href="${ctx}/task/${p.id}">${p.name}</a></li>
					</#list>
				</ul>
			</div>
			</#if>
		</form>
		<#--任务列表-->
			<#if list?? && list?size gt 0 >
				<#list list as task >
					<div class="div-task">
						<p>	
							<#--标题-->
							<a class="title" href="${ctx}/task/showTaskDetail/${task.id}" data-toggle="modal" data-target="#view-task-modal">${task.title}</a>
						</p>
						<p>	
							<#--状态-->
							<@taskLib.echoTaskStatus task.status />
							<#--分派人-->
							<#if task.assignUser?? >
							（任务接收人：<@taskLib.echoUsername task.assignUser/>）
							</#if>
							<#--创建人-->
							<span class="icon-user">
								由<@taskLib.echoUsername task.createUser/>创建
							</span>
							<#--最后更新时间和操作人-->
							<span class="icon-refresh">
								由<@taskLib.echoUsername task.updateUser/>最后更新于<span class="text-muted">${task.update_time}</span>
							</span>
							<#--完成时间-->
							<#if task.finish_time??>
								<span class="icon-time">&nbsp;完成于<span class="text-muted">${task.finish_time}</span></span>
							</#if>
							<#--编辑按钮，必须角色有权限且任务不是完成或关闭状态-->
							<#if 'ADMIN' == role! || 'TESTER' == role! >
							<#if 'FINISHED' != task.status && 'CLOSED' != task.status >
							<a data-toggle="modal" href="${ctx}/task/showUpdateTask/${task.id}" data-target="#updata-task-modal" title="编辑任务">
								<span class="icon-edit"></span>
							</a>
							</#if>
							</#if>
							<#--查看日志按钮-->
							&nbsp;&nbsp;
							<a data-toggle="modal" href="${ctx}/task/showLogList/${task.id}" data-target="#view-task-log-modal" title="查看日志">
								<span class="icon-list-alt"></span>
							</a>
						</p>		
					</div>
				</#list>
				${pageLink}
			<#else>
				<div class="alert alert-warning">无可显示数据</div>
			</#if>
		<#else>
		<div class="alert alert-warning">您尚未参与任何项目</div>		
		</#if>
		</div>
		<#--添加新任务模态框start-->
		<div class="modal fade" role="dialog" id="add-task-modal">
			<div class="modal-dialog  modal-lg">
				<div class="modal-content"></div>
			</div>
		</div>
		<#--更新任务模态框-->
		<div class="modal fade" role="dialog" id="updata-task-modal">
			<div class="modal-dialog modal-lg">
				<div class="modal-content"></div>
			</div>
		</div>
		<#--查看任务模态框-->
		<div class="modal fade" role="dialog" id="view-task-modal">
			<div class="modal-dialog  modal-lg">
				<div class="modal-content"></div>
			</div>
		</div>
		<#--查看任务日志模态框-->
		<div class="modal fade" role="dialog" id="view-task-log-modal">
			<div class="modal-dialog  modal-lg">
				<div class="modal-content"></div>
			</div>
		</div>
	</body>
</html>