<style type="text/css">
td.intro{
	max-width: 150px;
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}
</style>
<form class="form-inline" role="form" action="${ctx}/project/allProjects" name="projectListForm">
	<div class="form-group">
		<label for="search-project-name" class="sr-only">用户名称搜索</label>
		<input id="search-project-name" type="text" class="form-control" name="name" value="${name!}" placeholder="输入项目名称" maxlength="30"/>	
	</div>
	<button class="btn btn-default" type="submit">
		<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
	</button>
	<button class="btn btn-warning" type="button" data-toggle="modal" data-target="#add-project-modal">
		<span class="glyphicon glyphicon-plus"></span>&nbsp;添加新项目
	</button>
</form>
<div class="modal fade" role="dialog" id="add-project-modal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        		<h4 class="modal-title">添加新的项目</h4>
			</div>
			<form class="form-horizontal" role="form" style="margin-bottom:0;" action="${ctx}/project/saveProject" method="post" name="addProjectForm">
			<div class="modal-body">
				<div class="form-group">
					<label class="col-md-2 control-label" for="ipt-project-name">名称：</label>
					<div class="col-md-10">
						<input type="text" minlength="2" maxlength="30" id="ipt-project-name" name="name" class="form-control" placeholder="30字以内" required />
					</div>
				</div>
				<div class="form-group">
					<label class="col-md-2 control-label" for="textarea-project-intro">简介：</label>
					<div class="col-md-10">
						<textarea class="form-control" name="intro" id="textarea-project-intro"></textarea>
					</div>
				</div>
			</div>
			<div class="modal-footer" style="margin-top:0px;">
				<button class="btn btn-default" type="button" data-dismiss="modal">算了，放弃</button>
				<button class="btn btn-primary" type="submit">确定提交</button>
			</div>
			</form>
		</div>
	</div>
</div>
<#if list?? && list?size gt 0>
	<div class="table-responsive">
	<table class="table">
		<thead>
			<tr>
				<th>#</th>
				<th>名称</th>
				<th>参与人数</th>
				<th>创建时间</th>
				<th>简介</th>
			</tr>
		</thead>
		<tbody>
			<#list list as p >
			<tr>
				<td>${p_index+1}</td>
				<td>
					<a href="#menu-${p.id}" data-toggle="tooltip-menu" data-placement="bottom" data-container="#col-content" >${p.name}</a>
					<div class="tooltip-menu" id="menu-${p.id}" >
						<a class="list-group-item">查看项目成员</a>
						<a class="list-group-item">删除项目</a>
						<a class="list-group-item" data-dismiss="tooltip-menu" href="#">取消</a>
					</div>
				</td>
				<td>${p.u_count!0}</td>
				<td>${p.create_time}</td>
				<td class="intro"  data-toggle="popover" data-content="${p.intro}">
					<a href="#">${p.intro}</a>
				</td>
			</tr>
			</#list>
		</tbody>
	</table>
	</div>
<#else>
<div class="alert alert-warning">无可显示数据</div>
</#if>
<script type="text/javascript">
	$(document.projectListForm).ajaxForm({target:"#col-content"});
	$("td.intro").popover({placement:"left",container:"#col-content",trigger:"hover"});
	$("[data-toggle='tooltip-menu']").tooltipMenu();
	$(document.addProjectForm).validate({
		submitHandler:function(form){
			$(form).ajaxSubmit({
				success:function(json){
					if(!json.ok){
						showAlert(json.msg);
					}else{
						refresh();
					}
				}
			});
		}
	});
</script>