<div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h4 class="modal-title">修改项目<strong class="text-danger">${project.name}</strong>的简介</h4>
</div>
<form role="form" action="${ctx}/project/updateIntro" method="post" name="modifyProjectForm" style="margin-bottom:0;" >
<input type="hidden" name="projectId" value="${project.id}"/>
<div class="modal-body">
	<div class="form-group">
		<textarea rows="5" class="form-control" name="intro">${project.intro}</textarea>
	</div>
</div>
<div class="modal-footer">
	<button class="btn btn-default" type="button" data-dismiss="modal">算了，不改了</button>
	<button class="btn btn-primary">确认无误，提交</button>
</div>
</form>
<script type="text/javascript">
	$(document.modifyProjectForm).submit(function(){
		$(this).ajaxSubmit({
			success:function(json){
				if(!json.ok){
					showAlert(json.msg);
				}else{
					refresh();
				}
			}
		});
		return false;
	});
</script>