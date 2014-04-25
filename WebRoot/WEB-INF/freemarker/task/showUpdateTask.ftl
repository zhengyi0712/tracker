<#import "task.common.ftl" as taskLib/>
<div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h4 class="modal-title">修改任务</h4>
</div>
<div class="modal-body">
	<form role="form" action="${ctx}/task/updateTask" method="post" id="update-task-form" style="margin-bottom:0px;">
		<input type="hidden" name="task.id" value="${task.id}" />
		<input type="hidden" name="task.detail" value=""/>
		<div class="form-group">
			<label for="ipt-task-title" class="sr-only">标题：</label>
			<input type="text" class="form-control input-sm" value="${task.title}" id="ipt-task-title" name="task.title" maxlength="50" minlength="3" required placeholder="3-50个字符"/>
		</div>
		<div class="form-group">
			<label>标签：</label>
			<@taskLib.tagCheckbox tags task.tags />
		</div>
	</form>
		<div class="form-group">
			<label for="textarea-task-intro" class="sr-only">详细说明：</label>
			<textarea class="form-control" id="textarea-task-intro" style="height:300px;">${task.detail!}</textarea>
		</div>
</div>
<div class="modal-footer" style="margin-top:0;">
			<button class="btn btn-default btn-sm" data-dismiss="modal">取消</button>
			<button class="btn btn-primary btn-sm" onclick="submit()">确定提交</button>
</div>
<script type="text/javascript">
	if(um){
		um.destroy()
	}
	var um = UM.getEditor("textarea-task-intro");
	$("#update-task-form").validate({
		submitHandler:function(form){
			form["task.detail"].value = um.getContent();
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
	function submit(){
		$("#update-task-form").trigger("submit");
	}
</script>