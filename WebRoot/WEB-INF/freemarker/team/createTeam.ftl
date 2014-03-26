 <div class="modal-header">
     <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
     <h4 class="modal-title" id="myModalLabel">创建团队</h4>
</div>
<div class="modal-body">
       <form class="form-inline" action="${ctx}/team/saveTeamJson" method="post" name="teamForm">
       <div class="form-group">
       		<label for="team_name">团队名称：</label>
	       <input name="name" id="team_name" type="text" class="form-control" maxlength="50">
       </div>
       <button class="btn btn-primary">确定提交</button>
       
       </form>
</div>
<script type="text/javascript">
	$(document.teamForm).submit(function(){
		$(this).ajaxSubmit({
		dataType:"json",
		success:function(json){
			if(!json.ok){
				showAlert({
					title:"创建团队失败",
					content:json.msg
					});
				return;
			}
			location.reload();
		}
	});
	return false;
	});
</script>