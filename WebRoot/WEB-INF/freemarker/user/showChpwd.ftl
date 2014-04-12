<form id="chpwd-form" class="form-horizontal" role="form" action="${ctx}/user/updatePwdJSON" method="post" style="max-width:500px;">
	<div class="form-group">
		<label  for="oldPwd" class="control-label col-md-3">原密码：</label>
		<div class="col-md-9">
			<input type="password" class="form-control" name="oldPwd" id="oldPwd"/>
		</div>
	</div>
	<div class="form-group">
		<label class="control-label col-md-3" for="newPwd1">新密码：</label>
		<div class="col-md-9">
			<input type="password" class="form-control" name="newPwd1" id="newPwd1"/>
		</div>
	</div>
	<div class="form-group">
		<label class="col-md-3 control-label" for="newPwd2">再次确认：</label>
		<div class="col-md-9">
			<input type="password" class="form-control" name="newPwd2" id="newPwd2" equalTo="#newPwd1"/>
		</div>
	</div>
	<div class="form-group">
		<div class="col-md-9 col-md-offset-3">
			<button class="btn btn-primary" style="width:150px;">确认提交</button>
		</div>
	</div>
</form>
<script type="text/javascript">
	$("#chpwd-form").validate({
		submitHandler:function(form){
			$(form).ajaxSubmit({
				success:function(json){
					if(!json.ok){
						showAlert(json.msg);
					}else{
						showAlert({
							title:"修改密码",
							content:"密码修改成功，请点击确认重新登录",
							after:function(){
								location.reload(true);						
							}
						});
					}
				}
			});
		}
	});
</script>