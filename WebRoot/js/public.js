/**
 * 显示警告框
 * 
 * @param config
 *            配置参数对象，下面是各个参数说明： title:警告框的标题，默认会提示‘来自某站点的消息’
 *            content:警告框的文本内容，不能空，否则方法不会执行 after:点击确定后的回调函数
 *            btnText:确定按钮显示文本，默认是'确定'，自定义后按钮大小会根据文本自动变化并居中
 */
function showAlert(config) {
	if (!config) {
		return;
	}
	if ((typeof config) == "string") {
		showAlert({
			content : config
		});
		return;
	}
	config.type = "alert";
	showModal(config);
}
/**
 * 显示确认框
 * 
 * @param config
 *            配置参数对象，下面是各个参数说明： title:确认框的标题，默认会提示‘来自某站点的消息’
 *            content:确认框的文本内容，不能空，否则方法不会执行 ensure:点击确定后的执行回调函数
 *            cancel:点击取消后执行的回调函数 ensureText:确定按钮显示文本，默认是'确定'
 *            cancelText:取消按钮显示文本，默认是'取消'
 */
function showConfirm(config) {
	if (!config) {
		return;
	}
	if ((typeof config) == "string") {
		showConfirm({
			content : config
		});
		return;
	}
	config.type = "confirm";
	showModal(config);
}
function showModal(config) {
	if (!config.content) {
		return;
	}
	if (window._modal_id) {
		$("#modal-" + window._modal_id).remove();
	}
	if (!config.title) {
		config.title = "来自" + location.host + "的消息";
	}
	var id = (new Date()).getTime();
	var modal = document.createElement("div");
	var dialog = document.createElement("div");
	var content = document.createElement("div");
	var header = document.createElement("div");
	var body = document.createElement("div");
	var footer = document.createElement("div");
	$(modal).addClass("modal").addClass("fade").attr("id", "modal-" + id).attr(
			"role", "dialog").attr("aria-labelledby", "modal-title+" + id)
			.attr("aria-hidden", "true");
	$(dialog).addClass("modal-dialog");
	if ("sm" == config.size || "small" == config.size) {
		$(dialog).addClass("modal-sm");
	} else if ("lg" == config.size || "large" == config.size) {
		$(dialog).addClass("modal-lg");
	}
	$(modal).append(dialog);
	$(content).addClass("modal-content");
	$(dialog).append(content);
	$(header).addClass("modal-header");
	var title = document.createElement("h4");
	$(title).addClass("modal-title").attr("id", "modal-title+" + id).text(
			config.title);
	$(header).append(title);
	$(content).append(header);
	$(body).addClass("modal-body").html("<p>" + config.content + "<p>");
	$(content).append(body);
	$(footer).addClass("modal-footer");
	if ("confirm" == config.type) {
		var ensureBtn = document.createElement("button");
		var cancelBtn = document.createElement("button");
		$(ensureBtn).attr("type", "button").addClass("btn").addClass(
				"btn-primary").attr("data-dismiss", "modal");
		if (config.ensureText) {
			$(ensureBtn).text(config.ensureText);
		} else {
			$(ensureBtn).text("确 定").css("width", "100px");
		}
		$(ensureBtn).click(config.ensure);
		$(cancelBtn).attr("type", "button").addClass("btn").addClass(
				"btn-default").attr("data-dismiss", "modal");
		if (config.cancelText) {
			$(cancelBtn).text(config.cancelText);
		} else {
			$(cancelBtn).text("取 消").css("width", "100px");
		}
		$(cancelBtn).click(config.cancel);
		$(footer).append(cancelBtn).append(ensureBtn);
	} else {
		var btnWarp = document.createElement("div");
		var closeBtn = document.createElement("button");
		$(closeBtn).attr("type", "button").addClass("btn").addClass(
				"btn-primary").addClass("btn-block").attr("data-dismiss",
				"modal");
		if (config.btnText && (typeof config.btnText) == "string") {
			$(closeBtn).text(config.btnText);
			$(btnWarp).css("width", (config.btnText.length * 14 + 60) + "px");
		} else {
			$(btnWarp).css("width", "150px");
			$(closeBtn).text("确定");
		}
		$(closeBtn).click(config.after);
		$(btnWarp).addClass("container-fluid");
		$(btnWarp).append(closeBtn);
		$(footer).append(btnWarp);
	}
	$(content).append(footer);
	$(document.body).append(modal);
	$(modal).modal({
		backdrop : "static",
		keyboard : false
	});
	window._modal_id = id;
}
/**
 * 添加工具提示菜单方法
 */
$.fn.tooltipMenu = function() {
	$.each($(this),
			function() {
				if (!$(this).data("toggle")
						|| $(this).data("toggle") != "tooltip-menu") {
					return;
				}
				var target = $(this).data("target");
				if (!target) {
					target = $(this).attr("href");
				}
				if (!target) {
					return;
				}
				var targetObj = $(target);
				if (targetObj.length == 0) {
					return;
				}
				if (!targetObj.hasClass("tooltip-menu")) {
					return;
				}
				var title = "<div class='tooltip-menu'>" + targetObj.html()
						+ "</div>";
				$(this).tooltip({
					html : true,
					placement : $(this).data("placement"),
					title : title,
					trigger : "click",
					container : $(this).data("container")
				});
				// 关闭其它的气泡菜单
				$(this).bind(
						"show.bs.tooltip",
						function() {
							$("[data-toggle='tooltip-menu']:visible").not(
									$(this)[0]).tooltip("hide");
						});
				// 对于有属性data-dismiss为tooltip-menu的赋予关闭气泡菜单的功能
				$(this).bind(
						"shown.bs.tooltip",
						function() {
							var $this = $(this);
							if ($(this).data("bs.tooltip")) {
								$(this).data("bs.tooltip").tip().find(
										"[data-dismiss='tooltip-menu']").click(
										function() {
											$this.tooltip("hide");
										});
							}
						});
			});
};
// 为jQuery的ajax设置默认值
jQuery.ajaxSetup({
	error : function(xhr,textStatus,errorThrown) {
		var content = "请求发生错误，请尝试刷新后重试";
		var isUnLogin = xhr.getResponseHeader("login");
		if(isUnLogin){
			content = "登录超时，请点击确定后重新登录";
		}
		showAlert({
			title : "操作失败",
			content : content,
			after : function() {
				$(".modal").modal("hide");
				if(isUnLogin){
					location.reload();
				}
			}
		});
	}
});