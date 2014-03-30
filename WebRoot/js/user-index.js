var currentUrl = location.href;
$(function() {
	$("#user-menu a").click(function() {
		$("#user-menu a.active").removeClass("active");
		$(this).addClass("active");
		$("#user-index-title").html($(this).html());
	});
	// 为a标签赋予点击事件，令目标页面显示在id为col-content的div里面
	$("#row-body").on(
			"click",
			"a",
			function(e) {
				var href = $(this).attr("href");
				if (!href || href.substr(0, 1) == "#"
						|| href.indexOf("javascript:") != -1) {
					return;
				}
				// bootstrap定义的组件忽略
				if ($(this).data("toggle") || $(this).attr("role")) {
					return;
				}
				e.preventDefault();
				$("#col-content").load($(this).attr("href"));
				currentUrl = href;
			});
	// 触发牌选中状态的菜单的click事件
	$("#user-menu a.active").trigger("click");
});
function refresh() {
	$("#col-content").load(currentUrl);
	$(".modal").modal("hide");
	$(".modal-backdrop").hide();
}
