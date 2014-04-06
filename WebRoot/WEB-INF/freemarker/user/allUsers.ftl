<form class="form-inline" role="form" action="${ctx}/user/allUsers" name="userListForm">
	<div class="form-group">
		<label for="user-search-key" class="sr-only">用户搜索关键字</label>
		<input id="user-search-key" type="text" class="form-control" name="key" value="${key!}" placeholder="名字/邮箱/手机号" maxlength="30"/>
	</div>
	<button class="btn btn-default" type="submit">
		<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
	</button>
	<button class="btn btn-warning" type="button" data-toggle="modal" data-target="#add-user-modal">
		<span class="glyphicon glyphicon-plus"></span>&nbsp;添加新的用户
	</button>
</form>
<#--添加新用户模态框start-->
<div class="modal fade" id="add-user-modal" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        		<h4 class="modal-title">添加新的用户</h4>
			</div>
			<form class="form-horizontal" role="form" action="${ctx}/user/saveUser" style="margin-bottom:0;" name="userSaveForm" method="post">
			<div class="modal-body">
				<div class="form-group">
					<label class="control-label col-md-2" for="zhName">中文名：</label>
					<div class="col-md-10">
						<input type="text" class="form-control" name="zhName" minlength="2" maxlength="5" required id="zhName" placeholder="2-5个汉字"/>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-md-2" for="enName">英文名：</label>
					<div class="col-md-10">
						<input type="text" class="form-control" name="enName" minlength="2" maxlength="20" id="enName" placeholder="2-20个英文字母"/>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-md-2" for="email">邮箱：</label>
					<div class="col-md-10">
						<input type="email" class="form-control" name="email" required id="email"/>
					</div>
				</div>
				<div class="form-group">
					<label class="control-label col-md-2" for="mobile">手机号：</label>
					<div class="col-md-10">
						<input type="mobile" class="form-control" name="mobile" required id="mobile"/>
					</div>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-default" type="button" data-dismiss="modal">取消</button>
				<button class="btn btn-primary">确定保存</button>
			</div>
			</form>
		</div>
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
	$(document.userSaveForm).validate({
		rules:{
			zhName:{zhName:true},
			enName:{enName:true},
			mobile:{
				remote:"${ctx}/user/checkMobileExist"
			},email:{
				remote:"${ctx}/user/checkEmailExist"
			}
		},
		messages:{
			mobile:{
				remote:"手机号已经被使用了，请换一个试试"
			},email:{
				remote:"邮箱已经被使用了，请换一个试试"
			}
		},submitHandler:function(form){
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
	});
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
</script>