<#import '*/common.ftl' as common/>
<div class="modal-header">
	<button class="close" data-dismiss="modal" >x</button>
	<h4 class="modal-title">为团队<span class="text-danger">${team.name}</span>添加成员</h4>
</div>
<form role="form" class="form-horizontal" action="${ctx}/user/saveUserToTeam" style="margin-bottom:0;" id="addUserForm" method="post">
	<input type="hidden" name="teamId" value="${team.id}"/>
<div class="modal-body">
		<div class="form-group">
			<label class="col-md-2 control-label" form="cn_name">中文名：</label>
			<div class="col-md-9">
			<input type="text" class="form-control" name="zhName" id="cn_name" maxlength="5" minlength="2"/>		
			</div>
		</div>
		<div class="form-group">
			<label class="col-md-2 control-label" form="en_name">英文名：</label>
			<div class="col-md-9">
				<input type="text" class="form-control" name="enName" id="en_name" minlength="2" maxlength="20"/>
			</div>		
		</div>
		<div class="form-group">
			<label class="col-md-2 control-label" form="email">邮箱：</label>
			<div class="col-md-9">
				<input type="email" class="form-control" name="email" id="email" required  remote="${ctx}/user/checkEmailExist"/>
			</div>		
		</div>
		<div class="form-group">
			<label class="col-md-2 control-label" form="mobile">手机号：</label>
			<div class="col-md-9">
				<input type="text" class="form-control" name="mobile" id="mobile" required remote="${ctx}/user/checkMobileExist"/>
			</div>		
		</div>
		<div class="form-group">
			<label class="col-md-2 control-label">角色：</label>
			<div class="col-md-9">
				<div class="radio-inline">
					<label>
						<input type="radio" name="role" value="ordinary" checked="checked">普通成员
					</label>
				</div>
				<div class="radio-inline">
					<label>
						<input type="radio" name="role" value="admin">管理员
					</label>
				</div>
			</div>
		</div>
</div>
<div class="modal-footer">
	<button class="btn btn-default" data-dismiss="modal" type="button">取消</button>
	<button class="btn btn-primary" type="submit">保存新成员</button>
</div>
</form>
<@common.validateJs />
<script type="text/javascript">
	$("#addUserForm").validate({
		rules:{
			zhName:{
				required:true,
				zhName:true
			},enName:{
				enName:true
			},mobile:{
				mobile:true
			}
		},messages:{
			email:{
				remote:"邮箱已经被使用了，换一个吧"
			},mobile:{
				remote:"手机号已经被使用了，换一个吧"
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
</script>