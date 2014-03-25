/**
 * 显示警告框 
 * @param config 配置参数对象，下面是各个参数说明：
 * title:警告框的标题，默认会提示‘来自某站点的消息’
 * content:警告框的文本内容，不能空，否则方法不会执行
 * after:点击确定后的回调函数
 * btnText:确定按钮显示文本，默认是'确定'，自定义后按钮大小会根据文本自动变化并居中
 */
function showAlert(config){
	if(!config){
		return;
	}
	if((typeof config) == "string"){
		showAlert({content:config});
		return;
	}
	config.type="alert";
	showModal(config);
}
/**
 * 显示确认框 
 * @param config 配置参数对象，下面是各个参数说明：
 * title:确认框的标题，默认会提示‘来自某站点的消息’
 * content:确认框的文本内容，不能空，否则方法不会执行
 * ensure:点击确定后的执行回调函数
 * cancel:点击取消后执行的回调函数
 * ensureText:确定按钮显示文本，默认是'确定'
 * cancelText:取消按钮显示文本，默认是'取消'
 */
function showConfirm(config){
	if(!config){
		return;
	}
	if((typeof config) == "string"){
		showConfirm({content:config});
		return;
	}
	config.type="confirm";
	showModal(config);
}
function showModal(config){
	if(!config.content){
		return;
	}
	if(window._modal_id){
		$("#modal-"+window._modal_id).remove();
	}
	if(!config.title){
		config.title = "来自"+location.host+"的消息";
	}
	var id = (new Date()).getTime();
	var modal = document.createElement("div");
	var dialog = document.createElement("div");
	var content = document.createElement("div");
	var header = document.createElement("div");
	var body = document.createElement("div");
	var footer = document.createElement("div");
	$(modal).addClass("modal").addClass("fade").attr("id","modal-"+id)
		.attr("role","dialog").attr("aria-labelledby","modal-title+"+id)
		.attr("aria-hidden","true");
	$(dialog).addClass("modal-dialog");
	if("sm"==config.size||"small"==config.size){
		$(dialog).addClass("modal-sm");
	}else if("lg"==config.size||"large"==config.size){
		$(dialog).addClass("modal-lg");
	}
	$(modal).append(dialog);
	$(content).addClass("modal-content");
	$(dialog).append(content);
	$(header).addClass("modal-header");
	var title = document.createElement("h4");
	$(title).addClass("modal-title").attr("id","modal-title+"+id).text(config.title);
	$(header).append(title);
	$(content).append(header);
	$(body).addClass("modal-body").html("<p>"+config.content+"<p>");
	$(content).append(body);
	$(footer).addClass("modal-footer");
	if("confirm" == config.type){
		var ensureBtn = document.createElement("button");
		var cancelBtn = document.createElement("button");
		$(ensureBtn).attr("type","button").addClass("btn").addClass("btn-primary")
			.attr("data-dismiss","modal");
		if(config.ensureText){
			$(ensureBtn).text(config.ensureText);
		}else{
			$(ensureBtn).text("确 定").css("width","100px");
		}
		$(ensureBtn).click(config.ensure);
		$(cancelBtn).attr("type","button").addClass("btn").addClass("btn-default")
			.attr("data-dismiss","modal");
		if(config.cancelText){
			$(cancelBtn).text(config.cancelText);
		}else{
			$(cancelBtn).text("取 消").css("width","100px");
		}
		$(cancelBtn).click(config.cancel);
		$(footer).append(cancelBtn).append(ensureBtn);
	}else{
		var btnWarp = document.createElement("div");
		var closeBtn = document.createElement("button");
		$(closeBtn).attr("type","button").addClass("btn").addClass("btn-primary")
		.addClass("btn-block").attr("data-dismiss","modal");
		if(config.btnText&&(typeof config.btnText)=="string"){
			$(closeBtn).text(config.btnText);
			$(btnWarp).css("width",(config.btnText.length*14+60)+"px");
		}else{
			$(btnWarp).css("width","150px");
			$(closeBtn).text("确定");
		}
		$(closeBtn).click(config.after);
		$(btnWarp).addClass("container-fluid");
		$(btnWarp).append(closeBtn);
		$(footer).append(btnWarp);
	}
	$(content).append(footer);
	$(document.body).append(modal);
	$(modal).modal({backdrop:"static",keyboard:false});
	window._modal_id = id;
}
/**
 * 初始化激活工具提示菜单(tooltip-menu)
 */
$(function(){
	$.each($("[data-toggle='tooltip-menu']"),function(){
		var target = $(this).data("target");
		if(!target){
			target = $(this).attr("href");
		}
		if(!target){
			return;
		}
		var targetObj = $(target);
		if(targetObj.length==0){
			return;
		}
		if(!targetObj.hasClass("tooltip-menu")){
			return;
		}
		var title = "<div class='tooltip-menu'>"+targetObj.html()+"</div>";
		$(this).tooltip({
			html:true,
			placement:$(this).data("placement"),
			title:title,
			trigger:"click",
			container:$(this).data("container")
		});
		
	});
});