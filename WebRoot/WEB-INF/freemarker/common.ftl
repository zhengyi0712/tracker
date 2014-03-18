<#--html页面head标签内的引用部分，包含整个项目共用的js和css-->
<#macro headerReference >
	<meta http-equiv="charset" content="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
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
      <ul class="nav navbar-nav">
        <li class="dropdown active">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Bug列表 <b class="caret"></b></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="#">项目一</a></li>
                <li><a href="#">项目二</a></li>
                <li><a href="#">项目四</a></li>
              </ul>
        </li>
        <li>
          <a href="#">个人信息</a>
        </li>
        <li>
          <a href="#">修改密码</a>
        </li>
        <li>
          <a href="../javascript">管理</a>
        </li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
        <li><a href="">退出</a></li>
        <li><a href="">关于系统</a></li>
      </ul>
    </nav>
    </div>
 </div>
</#macro>
<#--页面的脚部，网站的声明信息和友情链接等-->
<#macro footer>

</#macro>
