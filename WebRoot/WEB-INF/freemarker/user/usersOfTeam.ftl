<#assign isAdmin = (role! == 'admin' || session.user.isAdmin)/>
<p>
	查看团队<strong class="text-danger">${team.name}</strong>的成员
	<#if isAdmin>
		<button class="btn btn-primary btn-xs" type="button" data-toggle="modal" data-target="#modal-add-user" data-remote="${ctx}/user/addUserToTeam/${team.id}">
			<span class="glyphicon glyphicon-plus"></span>&nbsp;创建新的团队成员
		</button>
		<button class="btn btn-warning btn-xs" type="button" data-toggle="modal" data-target="#modal-add-current-user" data-remote="${ctx}/user/addCurrentUserToTeam/${team.id}">
			<span class="glyphicon glyphicon-plus"></span>&nbsp;添加系统已有用户为团队成员
		</button>
	</#if>
</p>
<#--添加用户 模态框-->
<div class="modal fade" id="modal-add-user" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content"></div>
	</div>
</div>
<#--添加现有用户 模态框-->
<div class="modal fade" id="modal-add-current-user" role="dialog" aria-hidden="true">
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
			<td class="td-zh-name">
				<#if isAdmin>
					<a href="#menu-${user.id}" data-toggle="tooltip-menu" data-placement="bottom" data-container="#col-content">${user.zh_name}</a>
					<div class="tooltip-menu" id="menu-${user.id}">
						<a class="list-group-item" href="#" onclick="kick('${user.id}')">移出团队</a>
						<#if user.role == 'ordinary'>
							<a class="list-group-item" href="#" onclick="setRole('${user.id}','admin')">设为管理员</a>
						<#elseif user.role == 'admin'>
							<a class="list-group-item" href="#" onclick="setRole('${user.id}','ordinary')">设为普通成员</a>
						<#else>
						</#if>
						<a class="list-group-item" href="#" data-dismiss="tooltip-menu">取消</a>
					</div>
				<#else>
					${user.zh_name}
				</#if>
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
<script>
	$(".td-zh-name a").tooltipMenu();
	function kick(userId){
		var teamId = "${team.id}";
		$.getJSON("${ctx}/team/kickUser","teamId="+teamId+"&userId="+userId
			,function(json){
			if(!json.ok){
				showAlert(json.msg);
			}else{
				refresh();
			}
		});
	}
	function setRole(userId,role){
		var teamId = "${team.id}";
		$.post("${ctx}/team/setRole","teamId="+teamId+"&userId="+userId+"&role="+role
			,function(json){
			if(!json.ok){
				showAlert(json.msg);
			}else{
				refresh();
			}
		},"json");
	}
</script>