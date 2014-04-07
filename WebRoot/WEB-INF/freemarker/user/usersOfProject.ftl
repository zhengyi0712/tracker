<p>
	查看项目<strong class="text-danger">${project.name}</strong>成员
	<#if projectAdmin?? >
	<button class="btn btn-primary btn-xs" type="button" data-toggle="modal" data-target="#add-user-modal" data-remote="${ctx}/user/addUser?projectId=${project.id}">
		<span class="glyphicon glyphicon-plus-sign"></span>&nbsp;创建新的用户
	</button>
	<button class="btn btn-warning btn-xs" type="button" data-toggle="modal" data-target="#add-existing-user-modal">
		<span class="glyphicon glyphicon-plus"></span>&nbsp;添加系统已有用户
	</button>
	</#if>
</p>
<#if projectAdmin?? >
<#--添加用户模态框-->
<div class="modal fade" id="add-user-modal" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content"></div>
	</div>
</div>
<#--添加系统已经用户模态框-->
<div class="modal fade" id="add-existing-user-modal" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">添加系统现有用户到项目<span class="text-danger">${project.name}</span></h4>
			</div>
			<div class="modal-body">
				<form class="form-inline" role="form" name="userSearchForm" method="post" action="${ctx}/user/searchUserJSON">
					<input type="hidden" name="projectId" value="${project.id}"/>
					<div class="form-group">
						<input type="text" class="form-control" name="key" placeholder="名字/手机号/邮箱" />
					</div>
					<button type="submit" class="btn btn-primary">
						<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
					</button>
					<span class="text-muted">搜索结果不包含项目已有用户</span>
				</form>
				<form role="form" name="addExistingUserForm" action="${ctx}/user/addUsersToProject" style="margin-bottom:0;" method="post">
					<input type="hidden" name="projectId" value="${project.id}"/>
					<div class="form-group" id="add-existing-user-list"></div>
					<div class="form-group" id="add-existing-user-role" style="display:none;">
						<label class="radio-inline">
							<input type="radio" name="role" value="DEVELOPER" checked="checked">开发
						</label>
						<label class="radio-inline">
							<input type="radio" name="role" value="TESTER">测试
						</label>
						<label class="radio-inline">
							<input type="radio" name="role" value="ADMIN">管理员
						</label>
					</div>
				</form>
			</div>
			<div class="modal-footer" style="margin-top:0;">
				<span class="text-danger" id="error-message"></span>
				<button class="btn btn-default" data-dismiss="modal">取消</button>
				<button class="btn btn-primary" onclick="addExistintUser()">将选中的用户加入项目</button>
			</div>
		</div>
	</div>
</div>
<#--添加系统已经用户模态框end-->
</#if>
<#if list?? && list?size gt 0 >
	<div class="table-responsive">
		<table class="table">
			<thead>
				<tr>
					<th>角色</th>
					<th>中文名</th>
					<th>英文名</th>
					<th>邮箱</th>
					<th>手机号</th>
					<th>开户时间</th>
					<th>最后登录时间</th>
				</tr>
			</thead>	
			<tbody>
				<#list list as user>
				<tr>
					<td>
						<#if user.role == 'ADMIN'>
							管理员
						<#elseif user.role == 'DEVELOPER'>
							开发
						<#elseif user.role == 'TESTER'>
							测试
						<#else>
							未知
						</#if>
					</td>
					<td>${user.zh_name}</td>
					<td>${user.en_name!}</td>
					<td>${user.email}</td>
					<td>${user.mobile}</td>
					<td>${user.create_time}</td>
					<td>${user.login_time!}</td>
				</tr>
				</#list>
			</tbody>
		</table>	
	</div>
<#else>
<div class="alert alert-warning">无可显示数据</div>
</#if>
<script type="text/javascript">
	$(document.userSearchForm).submit(function(){
		$(this).ajaxSubmit({
			success:function(json){
				$("#add-existing-user-role").hide();
				var listDiv = $("#add-existing-user-list");
				listDiv.empty();
				if(!json || !json.list || json.list.length ==0 ){
					return;
				}
				var list = json.list;
				for(var i=0; i<list.length; i++){
					var user = list[i];
					var html = "<div class='checkbox'><label>";
					html += "<input type='checkbox' value='"+user.id+"' name='userId' checked='checked'/>";
					html += user.zh_name;
					if(user.en_name){
						html += " "+user.en_name;
					}
					html += " "+user.email+" "+user.mobile;
					html += "</label></div>";
					listDiv.append(html);
				}
				$("#add-existing-user-role").show();
			}
		});
		return false;
	});
	var messageTimeoutId = null;
	function addExistintUser(){
		var form = document.addExistingUserForm;
		if($("[name='userId']:checked").length==0){
			$("#error-message").text("没有选择任何用户");
			clearTimeout(messageTimeoutId);
			messageTimeoutId = setTimeout(function(){$("#error-message").empty();},3000);
			return;
		}
		$(form).ajaxSubmit({
			success:function(json){
				if(!json.ok){
					showAlert(json.msg);
				}else{
					refresh();
				}
			}
		});
	}
</script>