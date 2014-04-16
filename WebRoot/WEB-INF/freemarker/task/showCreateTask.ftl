<div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h4 class="modal-title">为项目<strong class="text-danger">${project.name}</strong>添加任务</h4>
</div>
<form role="form" action="${ctx}/task/saveTask" method="post" id="add-task-form" style="margin-bottom:0px;">
<div class="modal-body">
		<input type="hidden" name="task.project_id" value="${project.id}"/>
		<div class="form-group">
			<label for="ipt-task-title">标题：</label>
			<input type="text" class="form-control" id="ipt-task-title" name="task.title" maxlength="50" minlength="3" required placeholder="3-50个字符"/>
		</div>
		<div class="form-group">
			<label>标签：</label>
			<#list tags as tag>
				<label class="checkbox-inline">
					<input type="checkbox" name="tag" value="${tag}"/>${tag}
				</label>
			</#list>
		</div>
		<div class="form-group">
			<label for="textarea-task-intro">详细说明：</label>
			<textarea name="task.detail" class="form-control" id="textarea-task-intro"></textarea>
		</div>
</div>
<div class="modal-footer" style="margin-top:0;">
			<button class="btn btn-default" data-dismiss="modal">取消</button>
			<button class="btn btn-primary">确定提交</button>
</div>
</form>
<style type="text/css">
	.edui-container,.edui-body-container{
		width:100% !important;
	}
</style>
<script type="text/javascript">
	$("#textarea-task-intro").css("height","150px");
	var um = UM.getEditor("textarea-task-intro");
	$("#add-task-form").validate({
		submitHandler:function(form){
			$(form).ajaxSubmit({
				success:function(json){
					if(json.ok){
						location.reload();
					}else{
						showAlert(json.msg);
					}
				}
			});
		}
	});
</script>