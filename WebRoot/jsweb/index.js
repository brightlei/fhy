$(function(){
	$("#userinfo").html("欢迎你：<a href='javascript:void(0)' onclick='updateUserPwd()' style='color:#ffffff;text-decoration:underline;'>"+user.xm+"</a>");
	//alert(user.menupages);
	setCalendar();
	initMenu();
});

var userRolePages = [];
function initMenu(){
	var menupages = [
		{name:"fhyjf",title:"防护员积分",page:"fhyjf.html"},
		{name:"fhyjfjl",title:"防护员奖励分配",page:"fhyjfjl.html"},
		{name:"wzwj",title:"违章违纪录入",page:"wzwj.html"},
		{name:"wzwj_query",title:"违章违纪查询",page:"wzwj_query.html"},
		{name:"qgcjk",title:"全过程监控录入",page:"qgcjk.html"},
		{name:"qgcjk_query",title:"全过程监控查询",page:"qgcjk_query.html"},
		{name:"admin",title:"后台管理",page:"admin/index.html"}
	];
	var pageArr = user.menupages.split(",");
	var record = null;
	for(var i=0;i<menupages.length;i++){
		record = menupages[i];
		if($.inArray(record.page,pageArr)!=-1){
			if(record.page!="admin/index.html"){
				userRolePages.push(record);
			}else{
				$("#btnReload").before('<a id="adminBtn" href="admin/index.html" class="easyui-linkbutton normal" target="_blank" style="margin-right:5px;">后台管理</a>');
				$("#adminBtn").linkbutton({});
			}
		}
	}
	var selected = false;
	for(var i=0;i<userRolePages.length;i++){
		record = userRolePages[i];
		if(i==0){
			selected = true;
		}else{
			selected = false;
		}
		$('#mytab').tabs('add',{
		    title:record.title,
		    selected:selected,
		    content:''
		});
	}
}
//刷新当前页面
function refreshTab(){
	$("#"+selectTabFrameId).attr("src",selectTabPage);
}

//动态添加选择卡页面
function addTab(name,title,pagesrc){
	if(pagesrc.indexOf("?")!=-1){
		pagesrc = pagesrc+"&t="+Math.random();
	}else{
		pagesrc = pagesrc+"?t="+Math.random();
	}
	var content = '<iframe id="'+name+'" name="'+name+'" src="'+pagesrc+'" frameborder="0" scrolling="0" style="width:100%;height:100%;" onload="frameLoaded(this)"></iframe>';
	$('#mytab').tabs('add',{
	    title:title,
	    content:content,
	    closable:true
	});
}

//iframe加载完成事件
function frameLoaded(obj){
	$(obj).parent().css("overflow","hidden");
}

var selectTabFrameId = null;
var selectTabPage = null;
//选项卡选择事件
function selectTab(title){
	console.log(title);
	var record = null;
	var frameid = "";
	var pagesrc = "";
	for(var i=0;i<userRolePages.length;i++){
		record = userRolePages[i];
		if(record.title==title){
			frameid = record.name;
			pagesrc = record.page;
			break;
		}
	}
	if(frameid==""){
		return;
	}
	selectTabFrameId = frameid;
	selectTabPage = pagesrc;
	console.log("selectTabPage="+selectTabPage);
	if(document.getElementById(frameid)==null){
		var tab = $("#mytab").tabs("getTab",title);
		var content = '<iframe src="'+pagesrc+'" id="'+frameid+'" name="'+frameid+'" frameborder="0" scrolling="0" style="width: 100%;height:100%;"></iframe>';
		$("#mytab").tabs('update',{
			tab:tab,
			options:{
				content:content
			}
		});
		$("#"+frameid).parent().css("overflow","hidden");
	}else{
		console.log("[frameid]="+frameid);
		if(frameid=="wzwj" || frameid=="qgcjk"){
			window.frames[frameid].initDataGrid();
		}else if(frameid=="fhyjf" || frameid=="fhyjfjl"){
			$("#"+selectTabFrameId).attr("src",selectTabPage);
			console.log(selectTabFrameId+"|||"+selectTabPage);
		}
	}
	/**
	var tab_type = $("#mytab").find("div[title="+title+"]").attr("type");
	var frameid = "";
	var pagesrc = "";
	var isMainMenu = true;
	if(title=="防护员积分"){
		frameid = "fhyjf";
		pagesrc = "fhyjf.html";
	}else if(title=="违章违纪"){
		frameid = "wzwj";
		pagesrc = "wzwj.html";
	}else if(title=="全过程监控"){
		frameid = "qgcjk";
		pagesrc = "qgcjk.html";
	}else{
		isMainMenu = false;
	}
	if(!isMainMenu){
		return;
	}
	if(document.getElementById(frameid)==null){
		var tab = $("#mytab").tabs("getTab",title);
		var content = '<iframe src="'+pagesrc+'" id="'+frameid+'" name="'+frameid+'" frameborder="0" scrolling="0" style="width: 100%;height:100%;"></iframe>';
		$("#mytab").tabs('update',{
			tab:tab,
			options:{
				content:content
			}
		});
		$("#"+frameid).parent().css("overflow","hidden");
	}else{
		if(frameid=="wzwj" || frameid=="qgcjk"){
			window.frames[frameid].initDataGrid();
		}
	}
	*/
}
//退出登录
function logout(){
	window.location.href="login.html";
}
//全屏显示
function setFullWin(){
	$(document.body).layout("collapse","north");
	$("#btnFull").hide();
	$("#btnExitFull").show();
}
//退出全屏
function exitFullWin(){
	$(document.body).layout("expand","north");
	$("#btnFull").show();
	$("#btnExitFull").hide();
}
//加载收藏夹
function addFavorite() {
    var url = window.location;
    var title = document.title;
    var ua = navigator.userAgent.toLowerCase();
    if (ua.indexOf("msie 8") > -1) {
        external.AddToFavoritesBar(url, title, '');//IE8
    } else {
        try {
            window.external.addFavorite(url, title);
        } catch (e) {
            try {
                window.sidebar.addPanel(title, url, "");//firefox
            } catch (e) {
                alert("加入收藏失败，请使用Ctrl+D进行添加");
            }
        }
    }
}

var layerindex = 0;
//修改个人用户密码
function updateUserPwd(){
	layerindex = layer.open({
		type: 1,
		title: "个人账号密码修改",
		skin: 'layui-layer-rim', //加上边框
		area: ['500px', '274px'], //宽高
		content: $('#divform')
	});
}

//修改密码
function updatePwd(){
	$("#myform").form('submit',{
		url:"service/api",
		onSubmit: function(){
			var isValid = $(this).form('validate');
			if(isValid){
				var newpwd = $('#newpwd').textbox('getValue');
				var newpwd2 = $('#newpwd2').textbox('getValue');
				if(newpwd != newpwd2){
					layer.msg('新密码和确认密码不一致！');
					return false;
				}else if(newpwd.length<6){
					layer.msg('账号密码不能少于6位！');
					return false;
				}
				layer.msg("数据处理中，请稍候...",{icon:16,shade:[0.5,'#333333'],time:10000});
				$("#btnSave").linkbutton("disable");
				setTimeout(function(){
					$("#btnSave").linkbutton("enable");
				},5000);
			}
			return isValid;// 返回false终止表单提交
		},
		success: function(jsonstr){
			var json = eval('(' + jsonstr + ')');  
			if(json.code==0){
				var data = json.data;
				if(parseInt(data,10)==1){
					layer.close(layerindex);
					layer.alert("密码修改成功，请牢记新密码！",{icon:6});
				}else{
					layer.alert("密码修改失败！",{icon:5});
				}
			}else{
				layer.alert("密码修改失败！"+json.msg,{icon:5});
			}
		}
	});
}