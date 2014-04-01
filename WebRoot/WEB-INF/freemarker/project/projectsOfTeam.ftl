<#assign isAdmin = (role! == 'admin' || session.user.isAdmin)/>
<form class="form-inline" role="form">
	<label class="control-label">团队：<span class="text-warning">${team.name}</span></label>
	<label class="control-label">创建时间：<span class="text-warning">${team.create_time}</span></label>
	<#if isAdmin>
		<button class="btn btn-primary btn-xs" type="button" data-toggle="modal" data-target="#modal-add-project">
			<span class="glyphicon glyphicon-plus"></span>&nbsp;为团队创建新的项目
		</button>
	</#if>
</form>
<div class="modal fade" id="modal-add-project" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" data-dismiss="modal" >x</button>
				<h4 class="modal-title">为团队<span class="text-danger">${team.name}</span>创建项目</h4>
			</div>
			<div class="modal-body">
				<form class="form-inline" role="form" action="${ctx}/project/saveProject" method="post">
				<input type="hidden" valur="${team.id}" name="teamId"/>
					<div class="form-group">
						<input type="text" class="form-control" name="name" placeholder="输入项目名称"/>
					</div>
					<button type="submit" class="btn btn-primary">提交</button>
							
				</form>
			</div>
		</div>
	</div>
</div>
<#if !list?? || list?size == 0>
	<div class="alert alert-warning">该团队暂时没有项目</div>
<#else>
<div class="table-responsive">
	<table class="table">
		<thead>
		<tr>
			<td>#</td>
			<td>项目名称</td>
			<td>创建时间</td>
			<td>参与人数</td>
		</tr>
		</thead>
		<#list list as p>
		<tr>
			<td>${p_index + 1}</td>		
			<td>${p.name}</td>		
			<td>${p.create_time}}</td>		
			<td>${p.u_count}</td>		
		</tr>
		</#list>
	</table>
</div>
</#if>