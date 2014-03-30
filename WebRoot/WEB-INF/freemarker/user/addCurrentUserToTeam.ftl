<#import '*/common.ftl' as common/>
<div class="modal-header">
	<button class="close" data-dismiss="modal" >x</button>
	<h4 class="modal-title">为团队<span class="text-danger">${team.name}</span>添加成员</h4>
</div>
<div class="modal-body">
	<form class="form-inline" action="${ctx}/user/searchUserJSON" id="userSearchForm">
		<div class="form-group">
			<input name="key" type="text" placeholder="中文名/英文名/邮箱/手机" class="form-control"/>
		</div>
		<button type="submit" class="btn btn-primary">
			<span class="glyphicon glyphicon-search"></span>&nbsp;搜索
		</button>
	</form>
</div>
<script type="text/javascript">
	$("#userSearchForm").submit(function(){
		$(this).ajaxSubmit({
			success:function(json){
				if(json.list){
					var list = json.list;
					$("#div-user-list").remove();
					var div = $("<div></div>");
					div.addClass("list-group");
					div.attr("id","div-user-list");
					for(var i = 0; i<list.length; i++){
						var a = $("<a></a>");
						a.addClass("list-group-item");
						var html = list[i].zh_name + "&nbsp;";
						if(list[i].en_name){
							html += list[i].en_name + "&nbsp;";
						}
						html += list[i].email + "&nbsp;";
						html += list[i].mobile + "&nbsp;";
						a.html(html);
						a.attr("href","javascript:addUserToTeam('"+list[i].id+"');")
						div.append(a);
					}
					$("#userSearchForm").after(div);
				}
			}
		});
		return false;
	});
	function addUserToTeam(id){
		var teamId = "${team.id}";
		$.getJSON("${ctx}/user/setCurrentUserToTeam","teamId="+teamId+"&userId="+id,function(json){
			if(json.ok){
				refresh();
			}else{
				showAlert(json.msg);
			}
		});
	}
</script>