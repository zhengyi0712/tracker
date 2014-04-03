<#assign isAdmin = (role! == 'admin' || session.user.isAdmin)/>
<p>
	查看团队<strong class="text-danger">${team.name}</strong>的项目
	<#if isAdmin>
		<button class="btn btn-primary btn-xs" type="button" data-toggle="modal" data-target="#modal-add-project">
			<span class="glyphicon glyphicon-plus"></span>&nbsp;为团队创建新的项目
		</button>
	</#if>
</p>
<div class="modal fade" id="modal-add-project" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button class="close" data-dismiss="modal" >x</button>
				<h4 class="modal-title">为团队<span class="text-danger">${team.name}</span>创建项目</h4>
			</div>
			<div class="modal-body">
				<form class="form-inline" role="form" action="${ctx}/project/saveProject" method="post" name="projectForm">
				<input type="hidden" value="${team.id}" name="teamId"/>
					<div class="form-group">
						<input type="text" class="form-control" name="name" placeholder="输入项目名称" maxlength="50" minlength="3" required/>
					</div>
					<button type="submit" class="btn btn-primary">提交</button>
				</form>
			</div>
		</div>
	</div>
</div>
<#if !list?? || list?size == 0>
	<div class="alert alert-warning">该团队暂时没有项目</div>
<#else>
<div class="table-responsive">
	<table class="table">
		<thead>
		<tr>
			<th>#</th>
			<th>项目名称</th>
			<th>创建时间</th>
			<th>参与人数</th>
		</tr>
		</thead>
		<tbody>
		<#list list as p>
		<tr>
			<td>${p_index + 1}</td>		
			<td>
				<#if isAdmin>
				<a href="#menu-${p.id}" data-toggle="tooltip-menu" data-placement="bottom" data-container="#col-content">${p.name}</a>
				<div></div>
				<#else>
				</#if>
			</td>		
			<td>${p.create_time}</td>		
			<td>${p.u_count!0}</td>		
		</tr>
		</#list>
		</tbody>
	</table>
</div>
</#if>
<script type="text/javascript">
	$(document.projectForm).validate({
		submitHandler:function(form){
			$(form).ajaxSubmit({
				success:function(json){
					if(!json.ok){
						showAlert(json.msg);
						return;
					}
					refresh();
				}
			})
		}
	});

</script>