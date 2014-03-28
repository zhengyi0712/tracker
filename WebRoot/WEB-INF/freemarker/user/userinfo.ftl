<dl class="dl-horizontal">
	<dt>中文名：</dt><dd>${session.user.ch_name}</dd>
	<#if session.user.en_name??>
		<dt>英文名：</dt><dd>${session.user.en_name}</dd>
	</#if>
	<#if session.user.mobile??>
		<dt>手机号：</dt><dd>${session.user.mobile}</dd>
	</#if>
	<#if session.user.email??>
		<dt>电子邮箱：</dt><dd>${session.user.email}</dd>
	</#if>
	<dt>开户时间 ：</dt><dd>${session.user.create_time}</dd>
	<#if session.user.login_time??>
		<dt>上次登录 ：</dt><dd>${session.user.login_time}</dd>
	</#if>
</dl>