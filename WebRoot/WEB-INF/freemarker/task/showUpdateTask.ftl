<#import "task.common.ftl" as taskLib/>
<div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h4 class="modal-title">修改任务</h4>
</div>
<form role="form" action="${ctx}/task/updateTask" method="post" id="update-task-form" style="margin-bottom:0px;">
		<input type="hidden" name="task.id" value="${task.id}" />
<div class="modal-body">
		<div class="form-group">
			<label for="ipt-task-title" class="sr-only">标题：</label>
			<input type="text" class="form-control input-sm" value="${task.title}" id="ipt-task-title" name="task.title" maxlength="50" minlength="3" required placeholder="3-50个字符"/>
		</div>
		<div class="form-group">
			<label>标签：</label>
			<@taskLib.tagCheckbox tags task.tags />
		</div>
		<div class="form-group">
			<label for="textarea-task-intro" class="sr-only">详细说明：</label>
			<textarea name="task.detail" class="form-control" id="textarea-task-intro">${task.detail!}</textarea>
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
	}
</style>
<script type="text/javascript">
	$("#textarea-task-intro").css("height","150px");
	if(um){
		um.destroy()
	}
	var um = UM.getEditor("textarea-task-intro");
	$("#update-task-form").validate({
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