<#--html页面head标签内的引用部分，包含整个项目共用的js和css-->
<#macro headerReference >
	<meta http-equiv="charset" content="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
	<link rel="shortcut icon" type="image/x-icon" href="${ctx}/favicon.ico"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/lib/bootstrap/css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}/lib/bootstrap/css/bootstrap-theme.min.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}/lib/Font-Awesome-3.2.1/css/font-awesome.min.css" />
	<link rel="stylesheet" type="text/css" href="${ctx}/css/public.css" />
	
	<script type="text/javascript" src="${ctx}/lib/jquery/jquery-2.1.0.min.js"></script>
	<script type="text/javascript" src="${ctx}/lib/bootstrap/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${ctx}/lib/jquery/plugin/jquery.form.js"></script>
	<script type="text/javascript" src="${ctx}/js/public.js"></script>
</#macro>
<#--验证脚本引用-->
<#macro validateJs >
	<script type="text/javascript" src="${ctx}/lib/jquery/plugin/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${ctx}/lib/jquery/plugin/validate_message_zh.js"></script>
	<script type="text/javascript" src="${ctx}/js/custom-validate.js"></script>
</#macro>
<#--um编辑器脚本引用 -->
<#macro umJs>
	<link href="${ctx}/UM/themes/default/css/umeditor.min.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="${ctx}/UM/umeditor.config.js"></script>
	<script type="text/javascript" src="${ctx}/UM/umeditor.min.js"></script>
</#macro>
<#--阻止低版本IE访问-->
<#macro preventIE>
<!--[if lt IE 8]>
    <script type="text/javascript">alert("抱歉，系统检测到您还在使用低版本IE浏览器，请升级到最新版本或者使用其它浏览器访问。");window.close(); </script>
<![endif]-->
</#macro>
<#--页面的顶部导航条，包含网站的logo和各个菜单-->
<#macro topNavbar >
<div class="navbar navbar-default" role="navigation">
	<div class="container">
    <div class="navbar-header">
      <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#top-navbar">
        <span class="sr-only">伸缩导航条</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="${ctx}">Bugs Fly</a>
    </div>
    <nav class="collapse navbar-collapse" id="top-navbar" role="navigation">
      <ul class="nav navbar-nav navbar-right">
      	<li><p class="navbar-text text-primary"><strong>${session.user.ch_name}</strong></p></li>
      	 <li class="dropdown active">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Bug列表 <b class="caret"></b></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="#">项目一</a></li>
                <li><a href="#">项目二</a></li>
                <li><a href="#">项目四</a></li>
              </ul>
        </li>
        <li>
          <a href="../javascript">个人中心</a>
        </li>
        <li>
        	<a data-toggle="modal" data-target="#div-adout-system" href="#">关于系统</a>
        </li>
        <li>
        	<a href="javascript:showConfirm({title:'退出Bugs Fly',content:'确定要退出系统吗？',ensure:function(){location='${ctx}/login/logout'}});">退出</a>
        </li>
      </ul>
    </nav>
    </div>
 </div>
<#--关于系统模态框-->
<div class="modal fade" id="div-adout-system" tabindex="-1" role="dialog" aria-labelledby="about-system-title" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <h4 class="modal-title" id="about-system-title">关于系统</h4>
      </div>
      <div class="modal-body">
        <p>
        	<img src="${ctx}/images/logo.jpg" width="50px" height="40px"/>
        	<strong>Bugs Fly是一个简洁易用的bug跟踪系统，并且支持移动设备，在移动设备上可以完成所有操作，方便快捷。</strong>
        </p>
        <p><strong>源代码：</strong><a target="_blank" href="http://git.oschina.net/tai/bugs-fly">http://git.oschina.net/tai/bugs-fly</a></p>
        <p><strong>建议反馈：</strong><a href="mailto:taijunfeng_it@sina.com">taijunfeng_it@sina.com</a></p>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
</#macro>
<#--页面的脚部，网站的声明信息和友情链接等-->
<#macro footer>

</#macro>
