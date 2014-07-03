<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h4 class="modal-title">为项目<span class="text-danger">${project.name}</span>添加用户</h4>
</div>
<form class="form-horizontal" role="form" action="${ctx}/project/saveUser" style="margin-bottom:0;" name="userSaveForm" method="post">
	<input type="hidden" name="project.id" value="${project.id}"/>
<div class="modal-body">
	<div class="form-group">
		<label class="control-label col-md-2" for="zhName">中文名：</label>
		<div class="col-md-10">
			<input type="text" class="form-control" name="user.zh_name" minlength="2" maxlength="5" required id="zhName" placeholder="2-5个汉字"/>
		</div>
	</div>
	<div class="form-group">
		<label class="control-label col-md-2" for="enName">英文名：</label>
		<div class="col-md-10">
			<input type="text" class="form-control" name="user.en_name" minlength="2" maxlength="20" id="enName" placeholder="2-20个英文字母"/>
		</div>
	</div>
	<div class="form-group">
		<label class="control-label col-md-2" for="email">邮箱：</label>
		<div class="col-md-10">
			<input type="email" class="form-control" name="user.email" required id="email"/>
		</div>
	</div>
	<div class="form-group">
		<label class="control-label col-md-2" for="mobile">手机号：</label>
		<div class="col-md-10">
			<input type="mobile" class="form-control" name="user.mobile" required id="mobile"/>
		</div>
	</div>
	<div class="form-group">
		<label class="control-label col-md-2">角色：</label>
		<div class="col-md-10">
			<label class="radio-inline">
  				<input type="radio" name="project.role" value="DEVELOPER" checked="checked">开发
			</label>
			<label class="radio-inline">
  				<input type="radio" name="project.role" value="TESTER">测试
			</label>
			<label class="radio-inline">
  				<input type="radio" name="project.role" value="ADMIN">管理员
			</label>
		</div>
	</div>	
</div>
<div class="modal-footer">
	<span class="text-muted">帐号初始密码是手机号后六位</span>
	<button class="btn btn-default" type="button" data-dismiss="modal">取消</button>
	<button class="btn btn-primary">确定保存</button>
</div>
</form>
<script type="text/javascript">
$(document.userSaveForm).validate({
		rules:{
			"user.zh_name":{zhName:true},
			"user.en_name":{enName:true},
			"user.mobile":{
				remote:"${ctx}/user/checkMobile"
				},
			"user.email":{
				remote:"${ctx}/user/checkEmail"
				}
		},
		messages:{
			"user.mobile":{
				remote:"手机号已经被使用了，请换一个试试"
			},"user.email":{
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
</script>