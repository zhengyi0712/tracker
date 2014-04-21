<div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h4 class="modal-title">为项目<strong class="text-danger">${project.name}</strong>添加任务</h4>
</div>
<form role="form" action="${ctx}/task/saveTask" method="post" id="add-task-form" style="margin-bottom:0px;">
<div class="modal-body">
		<input type="hidden" name="task.project_id" value="${project.id}"/>
		<div class="form-group">
			<label for="ipt-task-title" class="sr-only">标题：</label>
			<input type="text" class="form-control input-sm" id="ipt-task-title" name="task.title" maxlength="50" minlength="3" required placeholder="标题3-50个字符"/>
		</div>
		<div class="form-group">
			<label>标签：</label>
			<#list tags as tag>
				<label class="checkbox-inline">
					<input type="checkbox" name="tagId" value="${tag.id}"/>${tag.name}
				</label>
			</#list>
		</div>
		<div class="form-group">
			<label for="textarea-task-intro-create" class="sr-only">详细说明：</label>
			<textarea name="task.detail" class="form-control" id="textarea-task-intro-create"></textarea>
		</div>
</div>
<div class="modal-footer" style="margin-top:0;">
			<button class="btn btn-default btn-sm" data-dismiss="modal">取消</button>
			<button class="btn btn-primary btn-sm">确定提交</button>
</div>
</form>
<style type="text/css">
	.edui-container,.edui-body-container{
		width:100% !important;
		min-height:200px !important;
	}
</style>
<script type="text/javascript">
	//避免与编辑任务冲突
	var umc = UM.getEditor("textarea-task-intro-create");
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