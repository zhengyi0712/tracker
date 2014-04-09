<style type="text/css">
	td.intro{
	max-width: 200px;
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
	cursor:default;
}
</style>
<form class="form-inline" role="form" action="${ctx}/project/myProjects" name="projectListForm">
	<div class="form-group">
		<label for="search-project-name" class="sr-only">用户名称搜索</label>
		<input id="search-project-name" type="text" class="form-control" name="name" value="${name!}" placeholder="输入项目名称" maxlength="30"/>	
	</div>
	<button class="btn btn-default" type="submit">
		<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
	</button>
</form>
<#if list?? && list?size gt 0 >
	<div class="table-responsive">
	<table class="table">
		<thead>
			<tr>
				<th>#</th>
				<th>项目名称</th>
				<th>我的角色</th>
				<th>人数</th>
				<th>创建时间</th>
				<th class="hidden-xs">简介</th>
			</tr>
		</thead>
		<tbody>
			<#list list as p >
			<tr>
				<td>${p_index+1}</td>
				<td>
					<a href="#menu-${p.id}" data-toggle="tooltip-menu" data-placement="bottom" data-container="#col-content" >${p.name}</a>
					<div class="tooltip-menu" id="menu-${p.id}" >
						<a class="list-group-item" href="${ctx}/user/usersOfProject/${p.id}">查看项目成员</a>
						<a class="list-group-item" href="${ctx}/task/${p.id}">查看任务</a>
						<#if p.role == 'ADMIN' >
						<a class="list-group-item" data-toggle="modal" data-target="#modify-intro-modal" href="${ctx}/project/modifyIntro/${p.id}">修改简介</a>
						</#if>
						<a class="list-group-item" data-dismiss="tooltip-menu" href="#">取消</a>
					</div>
				</td>
				<td>
						<#if p.role == 'ADMIN'>
							管理员
						<#elseif p.role == 'DEVELOPER'>
							开发
						<#elseif p.role == 'TESTER'>
							测试
						<#else>
							未知
						</#if>
				</td>
				<td>${p.u_count!0}</td>
				<td>${p.create_time}</td>
				<#if p.intro?? && p.intro?length gt 20 >
				<td class="intro hidden-xs"  data-toggle="popover" data-content="${p.intro}">
					${p.intro}
				</td>
				<#else>
				<td>${p.intro!}</td>
				</#if>
			</tr>
			</#list>
		</tbody>
	</table>
	${pageLink}
	</div>
<#else>
<div class="alert alert-warning">无可显示数据</div>
</#if>
<#--修改简介模态框-->
<div class="modal" role="dialog" id="modify-intro-modal">
	<div class="modal-dialog">
		<div class="modal-content"></div>
	</div>
</div>
<#--修改简介模态框end-->
<script type="text/javascript">
	$(document.projectListForm).ajaxForm({target:"#col-content"});
	$("a[data-toggle='tooltip-menu']").tooltipMenu();
	$("#modify-intro-modal").bind("hidden.bs.modal",function(e){
		$("#modify-intro-modal").removeData("bs.modal").find(".modal-content").empty();
	});
	$("td.intro").popover({placement:"left",container:"#col-content",trigger:"hover"});
</script>