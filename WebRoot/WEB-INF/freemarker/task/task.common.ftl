<#macro tag list >
	<#if !list?? || list?size == 0 >
		<#return>
	</#if>
	<#list list as tag >
		<#if tag == '错误'>
			<label class="label label-danger">${tag}</label>
		<#elseif tag == '优化'>
			<label class="label label-info">${tag}</label>
		<#elseif tag == '改善'>
			<label class="label label-success">${tag}</label>
		<#elseif tag == '新功能'>
			<label class="label label-warning">${tag}</label>
		<#else>
			<label class="label label-default">${tag}</label>
		</#if>
	</#list>
</#macro>