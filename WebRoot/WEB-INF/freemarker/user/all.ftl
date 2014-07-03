<form class="form-inline" role="form" action="${ctx}/user/all" name="userListForm">
	<div class="form-group">
		<label for="user-search-key" class="sr-only">用户搜索关键字</label>
		<input id="user-search-key" type="text" class="form-control" name="criteria" value="${criteria!}" placeholder="名字/邮箱/手机号" maxlength="30"/>
	</div>
	<button class="btn btn-default" type="submit">
		<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
	</button>
	<button class="btn btn-warning" type="button" data-toggle="modal" data-target="#add-user-modal" data-remote="${ctx}/user/add">
		<span class="glyphicon glyphicon-plus"></span>&nbsp;添加新的用户
	</button>
</form>
<#--添加新用户模态框start-->
<div class="modal fade" id="add-user-modal" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content"></div>
	</div>
</div>
<#--添加新用户模态框end-->
<#if list?? && list?size gt 0>
	<div class="table-responsive">
		<table class="table">
			<thead>
				<tr>
					<th>中文名</th>
					<th>英文名</th>
					<th>邮箱</th>
					<th>手机号</th>
					<th>开户时间</th>
					<th>状态</th>
					<th>最后登录时间</th>
					<th>操作</th>
				</tr>
			</thead>	
			<tbody>
				<#list list as user>
				<tr>
					<td>${user.zh_name}</td>
					<td>${user.en_name!}</td>
					<td>${user.email}</td>
					<td>${user.mobile}</td>
					<td>${user.create_time}</td>
					<td>
						<a href="#" data-userid="${user.id}" data-username="${user.zh_name}" onclick="toggleStatus(this)">
							${user.disabled?string('禁用','激活')}
						</a>
					</td>
					<td>${user.login_time!}</td>
					<td><a href="javascript:resetPwd('${user.id}','${user.zh_name}');">重置密码</a></td>
				</tr>
				</#list>
			</tbody>
		</table>
	</div>
	${pageLink}
<#else>
<div class="alert alert-warning">无可显示数据</div>
</#if>
<script type="text/javascript">
	$(document.userListForm).ajaxForm({target:"#col-content"});
	function toggleStatus(el){
		var username = $(el).data("username");
		var userId = $(el).data("userid");
		var status = $.trim($(el).text());
		var act = null;
		if("激活"==status){
			act = "禁用";
		}else{
			act = "激活";
		}
		showConfirm({
			title:act+"用户",
			content:"确定要将用户<strong class='text-danger'>"+username+"</strong>"+act+"吗？",
			ensureText:"是的我想清楚了",
			cancelText:"不好意思，我点错了",
			ensure:function(){
				$.getJSON("${ctx}/user/toggleStatus?userId="+userId
					,function(json){
						if(!json.ok){
							showAlert(json.msg);
							return;
						}
						$(el).text(act);
					}
				);			
			}
		});
		
	}
	function resetPwd(userId,username){
		showConfirm({
			title:"重置密码",
			content:"确定要将<strong class='text-danger'>"+username+"</strong>的密码重置吗？重置后密码变为手机号后六位。",
			ensure:function(){
				$.getJSON("${ctx}/user/resetPwd/"+userId,function(json){
					if(json.ok){
						refresh();
					}else{
						showAlert(json.msg);
					}
				});			
			}
		});
	}
</script>