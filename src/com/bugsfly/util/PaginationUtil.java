package com.bugsfly.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.plugin.activerecord.Page;

/**
 * 分页工具
 * 
 */
public class PaginationUtil {
	/**
	 * 生成分页的html代码
	 * 
	 * @param page
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String generatePaginateHTML(HttpServletRequest req,
			Page page) {
		String url = req.getRequestURI();
		StringBuilder html = new StringBuilder();
		int pn = page.getPageNumber();// 当前页码
		int tp = page.getTotalPage();// 总页数
		// 如果一共只有一页，不用生成链接了
		if (tp <= 1) {
			return "";
		}
		// 根据request参数生成url
		Map<String, String[]> parameterMap = req.getParameterMap();
		Set<String> keySet = parameterMap.keySet();
		Iterator<String> keyIter = keySet.iterator();
		StringBuilder params = new StringBuilder();
		// 把请求的参数拼接起来
		while (keyIter.hasNext()) {
			String key = keyIter.next();
			String[] values = parameterMap.get(key);
			// 如果是页码的参数，跳过
			if ("pn".equals(key)) {
				continue;
			}
			for (String value : values) {
				params.append(key + "=" + value + "&");
			}
		}
		if ("".equals(params.toString())) {
			url = url + "?pn=";
		} else {
			url = url + "?" + params + "pn=";
		}

		int ps = pn - 3, pe = pn + 3;// 起始页与结束页
		// 计算开始页与结束页
		if (ps < 1) {
			ps = 1;
		}
		if (pe > tp) {
			pe = tp;
		}
		// 拼接ul开始标签
		html.append("<ul class='pager'>");
		// 上一页
		if (pn > 1) {
			html.append("<li><a href='" + url + (pn - 1) + "'>上一页</a></li>");
		}
		// 首页
		if (ps > 1) {
			html.append("<li><a href='" + url + "1'>1</a></li>");
		}
		// 省略号
		if (ps > 2) {
			html.append("<li>...</li>");
		}
		// 生成从ps到pe的分页链接
		for (int i = ps; i <= pe; i++) {
			if(pn==i){
				html.append("<li class='active'><a>"+ i + "</a></li>");
			}else{
				html.append("<li><a href='" + url + i + "'>" + i + "</a></li>");
			}
		}
		// 省略号
		if (tp - pe > 1) {
			html.append("<li>...</li>");
		}
		// 最后一页
		if (pe < tp) {
			html.append("<li><a href='" + url + tp + "'>" + tp + "</a></li>");
		}
		// 下一页
		if (pn < tp) {
			html.append("<li><a href='" + url + (pn + 1) + "'>下一页</a></li>");
		}
		// 拼接ul结束标签
		html.append("</ul>");

		return html.toString();
	}
}
