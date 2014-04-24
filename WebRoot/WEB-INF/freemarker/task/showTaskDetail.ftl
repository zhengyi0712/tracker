<#import 'task.common.ftl' as taskLib />
<div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h4 class="modal-title">${task.title}</h4>
</div>
<div class="modal-body">
	${task.detail!'暂无任务详情说明'}
	<hr/>
	<p><strong>状态：</strong><@taskLib.echoTaskStatus task.status /></p>
	<#if task.tags?? && task.tags?size gt 0 >
	<p><strong>标签：</strong><@taskLib.echoTagLabel task.tags /></p>
	</#if>
	<p><strong>创建：</strong><span class="text-danger">${task.createUser.zh_name}<#if task.createUser.en_name?? >(${task.createUser.en_name})</#if></span>于<span class="text-muted">${task.create_time}</span>创建</p>
	<#--任务分派信息-->
	<#if 'ADMIN' == role! || task.assignUser?? >
	<div style="margin-bottom:10px;">
		<strong>分派：</strong>
		<#if task.assignUser??>
		已分派给<span class="text-danger">${task.assignUser.zh_name}<#if task.assignUser.en_name?? >(${task.assignUser.en_name})</#if></span>
		<#if task.finish_time?? >
		完成于<span class="text-muted">${task.finish_time}</span>
		</#if>
		</#if>
		<#if 'ADMIN' == role! && (task.status == 'ASSIGNED' || task.status == 'CREATED') >
		<div class="btn-group">
			<button  type="button" class="btn btn-default btn-xs" data-toggle="dropdown"><#if task.assignUser??>重新</#if>分派任务<span class="caret"></span></button>
			<ul class="dropdown-menu" role="menu">
				<#list project.developers as d>
				<li><a href="javascript:assign('${d.id}');">${d.zh_name}&nbsp;${d.en_name!}</a></li>
				</#list>
			</ul>
		</div>
		</#if>
	</div>
	</#if>
	<#--操作-->
	<#if 'ADMIN' == role!>
	<#--必须是未完成状态或者已完成状态并且可返工，不然管理员没有操作选项-->
	<#if reworkable?? || 'FINISHED' != task.status >
	<p>
		<strong>操作：</strong>
		<#if 'FINISHED' != task.status && 'CLOSED' != task.status >
			<button  type="button" class="btn btn-default btn-xs" data-id="${task.id}" onclick="finishTask(this)">设为已完成</button>		
		</#if>
		<#if reworkable??>
			<button  type="button" class="btn btn-default btn-xs" data-id="${task.id}" onclick="reworkTask(this)">将任务返工</button>
		</#if>
		<#if 'FINISHED' != task.status >
			<#if 'CLOSED' != task.status >
			<button type="button" class="btn btn-default btn-xs" data-id="${task.id}" onclick="closeTask(this)">关闭任务</button>
			</#if>
			<button  type="button" class="btn btn-danger btn-xs" data-id="${task.id}" onclick="deleteTask(this)">删除任务</button>
		</#if>
	</p>
	</#if>
	<#elseif session.user.id == task.assignUser.id!>
	<#--如果任务分派给了查看者本人，并且任务处于已分派或者返工状态，可设置为完成-->
		<#if 'ASSIGNED' == task.status || 'REWORKED' == task.status >
			<p><strong>操作：</strong><button  type="button" class="btn btn-default btn-xs" data-id="${task.id}" onclick="finishTask(this)">设为已完成</button></p>		
		</#if>
	</#if>
</div>
<script type="text/javascript">
	function assign(userId){
		$.post("${ctx}/task/assignTask","id=${task.id}&userId="+userId,
		function(json){
			if(json.ok){
				location.reload();
			}else{
				showAlert(json.msg);
			}
		},"json");
	}
	//设为已完成
	function finishTask(el){
		if($(el).data("executable")){
			$.getJSON("${ctx}/task/finishTask/"+$(el).data("id"),
			function(json){
				if(json.ok){
					location.reload();
				}else{
					showAlert(json.msg);
				}
			});
			return;
		}
		if($(el).data("time.id")){
			clearTimeout($(el).data("time.id"));
		}
		$(el).text("再点一次设为完成");
		$(el).data("executable",true);
		var timeId = setTimeout(function(){
			$(el).text("设为已完成");
			$(el).removeData("executable");
			$(el).removeData("time.id");
		},2000);
		$(el).data("time.id",timeId);
		
	}
	//将任务返工
	function reworkTask(el){
		if($(el).data("executable")){
			$.getJSON("${ctx}/task/reworkTask/"+$(el).data("id"),
			function(json){
				if(json.ok){
					location.reload();
				}else{
					showAlert(json.msg);
				}
			});
			return;
		}
		if($(el).data("time.id")){
			clearTimeout($(el).data("time.id"));
		}
		$(el).text("再点一将任务返工");
		$(el).data("executable",true);
		var timeId = setTimeout(function(){
			$(el).text("将任务返工");
			$(el).removeData("executable");
			$(el).removeData("time.id");
		},2000);
		$(el).data("time.id",timeId);
	}
	//关闭任务
	function closeTask(el){
		if($(el).data("executable")){
			$.getJSON("${ctx}/task/closeTask/"+$(el).data("id"),
			function(json){
				if(json.ok){
					location.reload();
				}else{
					showAlert(json.msg);
				}
			});
			return;
		}
		if($(el).data("time.id")){
			clearTimeout($(el).data("time.id"));
		}
		$(el).text("再点一将任务关闭");
		$(el).data("executable",true);
		var timeId = setTimeout(function(){
			$(el).text("关闭任务");
			$(el).removeData("executable");
			$(el).removeData("time.id");
		},2000);
		$(el).data("time.id",timeId);
	}
	//删除任务
	function deleteTask(el){
		if($(el).data("executable")){
			$.getJSON("${ctx}/task/deleteTask/"+$(el).data("id"),
			function(json){
				if(json.ok){
					location.reload();
				}else{
					showAlert(json.msg);
				}
			});
			return;
		}
		if($(el).data("time.id")){
			clearTimeout($(el).data("time.id"));
		}
		$(el).text("再点一将任务删除");
		$(el).data("executable",true);
		var timeId = setTimeout(function(){
			$(el).text("删除任务");
			$(el).removeData("executable");
			$(el).removeData("time.id");
		},2000);
		$(el).data("time.id",timeId);
	}

</script>