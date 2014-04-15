<div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h4 class="modal-title">为项目<strong class="text-danger">${project.name}</strong>添加任务</h4>
</div>
<div class="modal-body">
	<form role="form" action="${ctx}/task/saveTask" method="post">
		<input type="hidden" name="task.project_id" value="${project.id}"/>
		<div class="form-group">
			<label for="ipt-task-title">标题：</label>
			<input type="text" class="form-control" id="ipt-task-title" name="task.title" maxlength="50" minlength="3" required placeholder="3-50个字符"/>
		</div>
		<div class="form-group">
			<label for="textarea-task-intro">详细说明：</label>
			<textarea name="task.intro" class="form-control" id="textarea-task-intro"></textarea>
		</div>
	</form>
</div>
<div class="modal-footer" style="margin-top:0;">
	<button class="btn btn-default" data-dismiss="modal">取消</button>
	<button class="btn btn-primary">确定提交</button>
</div>
<style type="text/css">
	.edui-container,.edui-body-container{
		width:100% !important;
	}
</style>
<script type="text/javascript">
	$("#textarea-task-intro").css("height","150px");
		UM.getEditor("textarea-task-intro");
</script>