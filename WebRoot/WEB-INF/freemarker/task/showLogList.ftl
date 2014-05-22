<div class="modal-header">
	<button class="close" data-dismiss="modal">x</button>
	<h4 class="modal-title">查看任务<span class="text-danger">${task.title}</span>的日志</h4>
</div>
<div class="modal-body">
	<#if logList?? && logList?size gt 0>
	<ul>
		<#list logList as log>
			<li>${log.create_time?string("yyyy-MM-dd HH:mm:ss")}&nbsp;&nbsp;${log.content}</li>
		</#list>
	</ul>
	<#else>
		<p>任务无日志记录</p>
	</#if>
</div>