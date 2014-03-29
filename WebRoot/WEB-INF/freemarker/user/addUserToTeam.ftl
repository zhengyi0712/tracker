<#import '*/common.ftl' as common/>
<div class="modal-header">
	<button class="close" data-dismiss="modal" >x</button>
	<h4 class="modal-title">为团队<span class="text-danger">${team.name}</span>添加成员</h4>
</div>
<form role="form" class="form-horizontal" style="margin-bottom:0;" id="addUserForm" method="post">
<div class="modal-body">
		<div class="form-group">
			<label class="col-md-2 control-label" form="cn_name">中文名：</label>
			<div class="col-md-10">
			<input type="text" class="form-control" name="cnName" id="cn_name" maxlength="5" minlength="2"/>		
			</div>
		</div>
		<div class="form-group">
			<label class="col-md-2 control-label" form="en_name">英文名：</label>
			<div class="col-md-10">
				<input type="text" class="form-control" name="enName" id="en_name" minlength="2" maxlength="20"/>
			</div>		
		</div>
		<div class="form-group">
			<label class="col-md-2 control-label" form="email">邮箱：</label>
			<div class="col-md-10">
				<input type="email" class="form-control" name="email" id="email" required />
			</div>		
		</div>
		<div class="form-group">
			<label class="col-md-2 control-label" form="mobile">手机号：</label>
			<div class="col-md-10">
				<input type="text" class="form-control" name="mobile" id="mobile"/>
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
		debug:true,
		rules:{
			cnName:{
				required:true,
				cnName:true
			},enName:{
				enName:true
			},mobile:{
				required:true,
				mobile:true
			}
		}
	});
</script>