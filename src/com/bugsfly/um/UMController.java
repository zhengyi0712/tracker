package com.bugsfly.um;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

/**
 * UM即ueditor的mini版，UMController用于处理um交互的请求<br>
 * um返回状态默认SUCCESS（成功），其它表示非成功状态，直接写错误说明<br>
 */
public class UMController extends Controller {

	private String url;
	private String state = "SUCCESS";

	/**
	 * 图片上传，上传的图片会按照年份日期放在指定的文件夹里<br>
	 * um的状态对照表： <br>
	 * 
	 * @throws IOException
	 */
	public void imageUP() throws IOException {
		upload();
		String type = getPara("type");
		String editorId = getPara("editorid");
		if (type != null && "ajax".equals(type)) {
			renderText(url);
		} else {
			renderHtml("<script>parent.UM.getEditor('" + editorId
					+ "').getWidgetCallback('image')('" + url + "','" + state
					+ "')</script>");
		}
	}

	/**
	 * 上传方法
	 * 
	 * @throws IOException
	 */
	private void upload() throws IOException {
		// 根据当前日期获取应该存储的目录
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy"
				+ File.separator + "MMdd");
		String dir = dateFormat.format(new Date());
		String saveDir = JFinal.me().getConstants()
				.getUploadedFileSaveDirectory();
		if (!saveDir.endsWith(File.separator)) {
			saveDir = saveDir + File.separator;
		}
		File fileDir = new File(saveDir + dir);
		if (!fileDir.exists()) {
			if (!fileDir.mkdirs()) {
				state = "目录创建失败";
				return;
			}
		}
		UploadFile uploadFile = getFile("upfile", dir);
		String fileName = uploadFile.getOriginalFileName();
		if (!checkFileType(fileName)) {
			state = "不支持的文件格式";
			uploadFile.getFile().delete();
			return;
		}
		// 将文件改名，这么做是为了防止重名
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		String newFileName = System.currentTimeMillis() + ""
				+ new Random().nextInt();
		newFileName = saveDir + dir + File.separator + newFileName + suffix;
		uploadFile.getFile().renameTo(new File(newFileName));
		// 通过处理得到图片的访问路径
		url = newFileName.replace(PathKit.getWebRootPath(), "");
		url = url.replace(File.separator, "/");
		if (!url.startsWith("/")) {
			url = "/" + url;
		}
		url = getSession().getServletContext().getContextPath() + url;

	}

	/**
	 * 检查文件格式，仅仅是根据文件名
	 * 
	 * @param fileName
	 * @return
	 */
	private boolean checkFileType(String fileName) {
		String[] fileTypes = new String[] { ".gif", ".png", ".jpg", ".jpeg",
				".bmp" };
		for (String fileType : fileTypes) {
			if (fileName.endsWith(fileType)) {
				return true;
			}
		}
		return false;
	}
}
