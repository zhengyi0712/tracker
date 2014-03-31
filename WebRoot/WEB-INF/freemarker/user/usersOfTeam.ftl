<#assign isAdmin = (role == 'admin' || session.user.isAdmin)/>
<form class="form-inline" role="form">
	<label class="control-label">团队：<span class="text-warning">${team.name}</span></label>
	<label class="control-label">创建时间：<span class="text-warning">${team.create_time}</span></label>
	<#if isAdmin>
		<button class="btn btn-primary btn-xs" type="button" data-toggle="modal" data-target="#modal-add-user" data-remote="${ctx}/user/addUserToTeam/${team.id}">
			<span class="glyphicon glyphicon-plus"></span>&nbsp;创建新的团队成员
		</button>
		<button class="btn btn-warning btn-xs" type="button" data-toggle="modal" data-target="#modal-add-user" data-remote="${ctx}/user/addCurrentUserToTeam/${team.id}">
			<span class="glyphicon glyphicon-plus"></span>&nbsp;添加系统已有用户为团队成员
		</button>
	</#if>
</form>
<#--添加用户 模态框-->
<div class="modal fade" id="modal-add-user" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content"></div>
	</div>
</div>
<#if !list?? || list?size==0 >
	<div class="alert alert-warning">团队还没有成员</div>
<#else>
	<div class="table-responsive">
	<table class="table">
		<thead>
			<tr>
				<th>中文名</th>		
				<th>英文名</th>
				<th>角色</th>
				<th>邮箱</th>		
				<th>手机</th>		
				<th>开户时间</th>		
				<th>最后登录时间</th>		
			</tr>
		</thead>
		<tbody>
		<#list list as user >
		<tr>
			<td>
				<a href="#">${user.zh_name}</a>
			</td>
			<td>${user.en_name}</td>
			<td>
				<#if user.role == 'ordinary'>
					普通成员
				<#elseif user.role == 'admin'>
					管理员
				<#else>
					未知
				</#if>
			</td>
			<td>${user.email}</td>
			<td>${user.mobile}</td>
			<td>${user.create_time}</td>
			<td>${user.login_time!}</td>
		</tr>		
		</#list>
		</tbody>
	</table>
	</div>
</#if>