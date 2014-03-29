<form class="form-inline" role="form">
	<label class="control-label">团队：<span class="text-warning">${team.name}</span></label>
	<label class="control-label">创建时间：<span class="text-warning">${team.create_time}</span></label>
	<button class="btn btn-primary btn-sm" type="button" data-toggle="modal" data-target="#modal-add-user" data-remote="${ctx}/user/addUserToTeam/${team.id}">
		<span class="glyphicon glyphicon-plus"></span>&nbsp;添加成员
	</button>
</form>
<#--添加用户 模态框-->
<div class="modal fade" id="modal-add-user" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content"></div>
	</div>
</div>
<#if list?? || list?size==0 >
	<div class="alert alert-warning">无可显示数据</div>
<#else>
	<table class="table table-responsive">
		<tr>
			<th>中文名</th>		
			<th>英文名</th>		
			<th>开户时间</th>		
			<th>最后登录时间</th>		
			<th>操作</th>		
		</tr>
		<#list list as user >
		<tr>
			<td>${user.cn_name}</td>
			<td>${user.en_name}</td>
			<td>${user.create_time}</td>
			<td>${user.login_time}</td>
			<td><a>移出</a></td>
		</tr>		
		</#list>
	</table>
</#if>