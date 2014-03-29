/**
 *自定义验证，对jquery validate进行一些默认设置，增加验证方法 
 */
jQuery.validator.setDefaults({
	highlight:function(element,errorClass){
		$(element).parent().removeClass("has-success").addClass("has-error");
	},unhighlight:function(element,errorClass){
		$(element).parent().removeClass("has-error").addClass("has-success");
	},errorClass:"text-danger"
});
jQuery.validator.addMethod("notOnlyDigits", function(val, el, params) {
	var pattern = /^\D+$/;
	return this.optional(el) || pattern.test(val);
}, "字段不能是纯数字组成");
jQuery.validator.addMethod("notOnlyLetters", function(val, el, params) {
	var pattern = /^[a-zA-Z]+$/;
	return this.optional(el) || !pattern.test(val);
}, "字段不能由纯字母组成");
jQuery.validator.addMethod("exIllegalChars", function(val, el, params) {
	var pattern = /[\,\?\/\\'";:\[\]\{\}`]/;
	return this.optional(el) || !pattern.test(val);
}, "不能包含非法字符");
jQuery.validator.addMethod("notUnderscoreStart", function(val, el, params) {
	return this.optional(el) || "_" != val.substr(0, 1);
}, "不能以下划线开头");
jQuery.validator.addMethod("notUnderscoreEnd", function(val, el, params) {
	return this.optional(el) || "_" != val.substr(val.length - 1, 1);
}, "不能以下划线结尾");
jQuery.validator.addMethod("password", function(val, el, params) {
	var pattern = /^[\w!@#\$%\^&\*\(\)\+\-=`~\.]{6,16}$/;
	return pattern.test(val);
}, "密码必须是6-16位字符，由字母数字符号组成");
jQuery.validator.addMethod("account", function(val, el, params) {
	var pattern1 = /^1[3458]\d{9}$/;
	var pattern2 = /^([a-zA-Z0-9_\.\-])+@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	return this.optional(el) ||pattern1.test(val)||pattern2.test(val);
},"请输入格式正确的手机号或邮箱");
jQuery.validator.addMethod("mobile", function(val, el, params) {
	var pattern = /^1[3458]\d{9}$/;
	return this.optional(el) || pattern.test(val);
}, "请输入正确的11位手机号");
jQuery.validator.addMethod("phone", function(val, el, params) {
	var pattern = /^(\d{3,4}\-?)?\d{7,8}$/;
	return this.optional(el) || pattern.test(val);
}, "请输入正确的固话号码");
jQuery.validator.addMethod("tel", function(val, el, params) {
	var pattern1 = /^(\d{3,4}\-?)?\d{7,8}$/;
	var pattern2 = /^1[3458]\d{9}$/;
	return this.optional(el) || pattern1.test(val) || pattern2.test(val);
}, "请输入正确的手机号码或固话号码");
jQuery.validator.addMethod("zhName", function(val, el, params) {
	var pattern = /^[\u4e00-\u9fa5]{2,5}$/;
	return this.optional(el) || pattern.test(val);
},"中文名必须是2-5个汉字，不支持生僻字");
jQuery.validator.addMethod("enName", function(val, el, params) {
	var pattern = /^[a-zA-Z]{2,20}$/;
	return this.optional(el) || pattern.test(val);
},"英文名必须是2-20个字母");