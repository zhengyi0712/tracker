<#macro echoTagLabel list >
	<#if !list?? || list?size == 0 >
		<#return>
	</#if>
	<#list list as tag >
		<#if tag.id == 'BUG'>
			<label class="label label-danger">${tag.name}</label>
		<#elseif tag.id == 'OPTIMIZATION'>
			<label class="label label-info">${tag.name}</label>
		<#elseif tag.id == 'IMPROVE'>
			<label class="label label-success">${tag.name}</label>
		<#elseif tag.id == 'FEATURE'>
			<label class="label label-warning">${tag.name}</label>
		<#else>
			<label class="label label-default">${tag.name}</label>
		</#if>
	</#list>
</#macro>

<#macro echoTaskStatus status>
	<#if 'CREATED' == status >
		<span class="icon-file-alt"> 新建</span>
	<#elseif 'ASSIGNED' == status>
		<span class="icon-hand-right"> 已分派</span>
	<#elseif 'FINISHED' == status>
		<span class="icon-check"> 已完成</span>
	<#elseif 'REWORKED' == status>
		<span class="icon-undo"> 已返工</span>
	<#elseif 'CLOSED' == status>
		<span class="icon-remove-circle"> 已关闭</span>
	</#if>
</#macro>

<#macro tagCheckbox  tags checkedTags >
	<#list tags as tag>
		<label class="checkbox-inline">
			<input type="checkbox" name="tagId" value="${tag.id}" 
				<#if checkedTags?? && checkedTags?size gt 0 >
					<#list checkedTags as ct >
						<#if ct.id == tag.id >
							checked="checked"
							<#break>
						</#if>
					</#list>
				</#if>
			/>${tag.name}	
		</label>
	</#list>
</#macro>