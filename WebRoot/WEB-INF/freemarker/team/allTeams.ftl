<form class="form-inline" role="form" action="${ctx}/team/allTeams" name="teamSearchForm">
	<div class="form-group">
		<input type="text" placeholder="团队名称" class="form-control" name="name" value="${name!}">
	</div>
	<button class="btn btn-default" type="submit">
		<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
	</button>
	<a class="btn btn-primary" href="${ctx}/team/createTeam" data-toggle="modal" data-target="#div-create-team">
		<span class="glyphicon glyphicon-plus"></span>&nbsp;创建团队
	</a>
</form>
<#--创建团队模态框-->
<div class="modal fade" id="div-create-team" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content"></div>
	</div>
</div>
<#--列表-->
<#if !page.list?? || page.list?size == 0 >
	<div class="alert alert-warning">无可显示数据</div>
<#else>
	<table class="table table-responsive">
		<thead>
			<tr>
				<th>序号</th>
				<th>团队名称</th>
				<th>创建时间</th>
				<th>项目数</th>
				<th>人数</th>
			</tr>
		</thead>
		<tbody>				
			<#list page.list as team>
			<tr>
				<td>${team_index+1}</td>							
				<td class="dropdown">
					<a data-toggle="tooltip-menu" data-placement="auto bottom" href="#team-menu-${team.id}" data-container="body">${team.name}</a>
					<ul class="tooltip-menu" id="team-menu-${team.id}">
						<a class="list-group-item">菜单一</a>
						<a class="list-group-item">菜单一</a>
						<a class="list-group-item">菜单一</a>
						<a class="list-group-item">菜单一</a>
					</ul>
				</td>							
				<td>${team.create_time}</td>							
				<td>${team.p_count!0}</td>							
				<td>${team.u_count!0}</td>
			</tr>
			</#list>
		</tbody>
	</table>
	${pageLink}
</#if>
<script type="text/javascript">
	$(document.teamSearchForm).ajaxForm({target:"#col-content"});
	$("td.dropdown a").tooltipMenu();
</script>				