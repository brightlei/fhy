//上传文件的参数
var uploadParam = {zbid:0};
//初始化文件上传控件
layui.use('upload', function() {
	var upload = layui.upload;
	//执行实例
	var uploadInst = upload.render({
		elem : '#btn_upload' //绑定元素
		,url : '../service/api?action=importPjzbData' //上传服务接口
		,accept : 'file'
		,ext:'xls|XLS',
		data : uploadParam,
		before:function(obj){
			uploadParam.zbid = $("#zbid").val();
			console.log(uploadParam);
			top.layer.msg("正在批量导入数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
		},
		done : function(result) {//上传完毕回调 200
			var code = result.code;
			if(code==0){
				loadPjzbData();
				top.layer.alert("成功导入更新["+result.data+"]条数据！",{icon:6});
			}else{
				top.layer.alert("批量导入更新数据失败！"+result.msg,{icon:5});
			}
		},
		error : function() {//请求异常回调
			top.layer.alert("批量导入更新数据出现异常！");
		}
	});
});

$(function(){
	$("#tbForm td").css("padding", "10px");
	initDataGrid();
	loadData();
});

//加载评价指标数据
function loadData(){
	$("#menuTree").html("正在加载数据，请稍候......");
	doAjax("../service/api",{action:"loaddata",method:"queryPjzb"},{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["treeData"]=data;
			initTree();
		}
	});
}

//创建树结构
function initTree(){
	var data = cache["treeData"];
	var treeData = getTreeChildren("0");
	$("#menuTree").tree({
		animate:false,
		lines:true,
		onContextMenu: function(e,node){
			e.preventDefault();
			$(this).tree('select',node.target);
			$('#mm').menu('show',{
				left: e.pageX,
				top: e.pageY
			});
		},onClick:function(node){
			editNode = node;
			$("#id").val(node.id);
			$("#zbid").val(node.id);
			$("#input_name").textbox("setValue",node.text);
			$("#input_zbgs").textbox("setValue",node.zbgs);
			$("#btn_upload").linkbutton('enable');
			loadPjzbData();
		},onLoadSuccess:function(node,data){
			$("#menuTree").tree("select",data[0].target);
		}
	});
	$("#menuTree").tree("loadData",treeData);
	$("#menuTree .tree-node").css("height","24px");
}
//获取树节点孩子节点
function getTreeChildren(pid){
	var treeArr = new Array();
	var treeData = cache["treeData"];
	var count = treeData.length;
	for(var i=0;i<count;i++){
		var record = treeData[i];
		if(record.pid==pid){
			var treeNode = new Object();
			treeNode.id = record.id;
			treeNode.text = record.name;
			treeNode.zbgs = record.zbgs;
			treeNode.rank = record.rank;
			treeNode.pid = record.pid;
			if(pid=="0"){
				treeNode.iconCls = "icon-hamburg-home";
			}else{
				treeNode.iconCls = "icon-hamburg-project";
			}
			treeNode.children = getTreeChildren(record.id);
			treeArr.push(treeNode);
		}
	}
	//根据排序进行排序
	treeArr =treeArr.sort(sortByAtt("rank","desc"));
	return treeArr;
}

//添加下级树节点
function appendTreeNode(type){
	top.layer.prompt({
		title:"请输入你要添加的评价指标名称"
	},function(value,index,elem){
		top.layer.close(index);
		var nodeText = value;
		var t = $('#menuTree');
		var node = t.tree('getSelected');
		var pid = node.id;
		if(type=="child"){
			pid = node.id;
		}else{
			pid = node.pid;
		}
		var param = new Object();
		param.action = "saveOrUpdate";
		param.name = nodeText;
		param.pid = pid;
		param.zbgs = "";
		param.method = "addPjzb";
		var children = t.tree("getChildren",node.target);
		var count = children.length;
		param.rank = 100-count;
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax("../service/api",param,{},
		function(rsMap,op){
			top.layer.close(top.layerIndex);
			if(parseInt(rsMap.data,10) == 1){
				top.layer.msg("添加评价指标成功！",{icon:6});
			}
			loadData();
		});
	});
}

//修改功能菜单节点
function updateMenu(){
	var paramArr = new Array();
	var id = $("#id").val();
	var name = $("#input_name").textbox('getValue');
	var zbgs = $("#input_zbgs").textbox('getValue');
	var param = new Object();
	param.id = id;
	param.name = name;
	param.zbgs = zbgs;
	param.method = "editPjzb";
	param.action = "saveOrUpdate";
	if(id==null||id==""){
		top.layer.msg("请从左边树结构中选择你要修改的评价指标信息！");
		return;
	}
	var t = $('#menuTree');
	var treeNode = t.tree('find',id);
	top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
	doAjax("../service/api",param,{},function(rsMap,op){
		top.layer.close(top.layerIndex);
		if(rsMap.data=="1"){
			//loadData();
			$('#menuTree').tree('update', {
				target: treeNode.target,
				text: param.name,
				zbgs: param.zbgs
			});
			top.layer.alert("评价指标信息修改成功！",{icon:6});
		}else{
			top.layer.alert("评价指标信息修改失败！",{icon:5});
		}
	});
}
//删除部门信息
function removeit(){
	var node = $('#menuTree').tree('getSelected');
	//获取所有根节点数据
	var roots = $('#menuTree').tree('getRoots');
	if(roots.length == 1){
		top.layer.msg("最后一个根节点不允许删除！");
		return;
	}
	var isLeaf = $("#menuTree").tree('isLeaf',node.target);
	var tipmsg = "";
	if(isLeaf){
		tipmsg = "确定要删除该评价指标信息吗？删除该评价指标会同时删除下面所有的指标评分数据！";
	}else{
		tipmsg = "确定要删除该评价指标信息吗？删除该评价指标会同时删除下级所有评价指标及指标评分数据！";
	}
	top.layer.confirm(tipmsg,{icon:3},function(index){
		top.layer.close(index);
		var param = new Object();
		param.action = "saveOrUpdate";
		param.method="deletePjzb";
		param.id = node.id;
		top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
		doAjax("../service/api",param,{},function(rsMap,op){
			top.layer.close(top.layerIndex);
			var data = rsMap.data;
			if(data>0){
				$('#menuTree').tree('remove',node.target);
				//loadData();
				top.layer.alert("评价指标信息删除成功！",{icon:6});
			}else{
				top.layer.alert("评价指标信息删除失败！",{icon:5});
			}
		});
	})
}
//初始化部门人员列表
function initDataGrid(){
	$("#dg").datagrid({
		title:"评价指标数据列表",
		rownumbers:true,
		//singleSelect:true,
		autoRowHeight:false,
		striped:true,
		nowrap:false,
		fit:true,
		remoteSort:false,
		toolbar:'#tb',
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
		    {field:'op',title:'操作',width:'50',align:'center',
		    	formatter:function(value,row,index){
			    	return '<button class="layui-btn layui-btn-normal layui-btn-xs" onclick="editPjzbData('+index+')">修改</button>';
			}},
			{field:'id',title:'编号',width:'40',hidden:true},
			{field:'pjtk',title:'评价条款',width:'290'},
			{field:'pf',title:'评分',width:'80',align:"center"},
			{field:'qz1',title:'权重1',width:'70',align:"center"},   
			{field:'qz2',title:'权重2',width:'70',align:"center"},   
			{field:'createtime',title:'创建时间',width:150,align:"center"},
			{field:'edittime',title:'修改时间',width:150,align:"center"}
		]]
	});
}
//获取评价指标评分数据
function loadPjzbData(){
	var zbid = $("#zbid").val(); 
	var param = new Object();
	param.action = "loaddata";
	param.method="queryPjzbData";
	param.zbid = zbid;
	top.layerIndex=top.showLayerLoading("请求中处理中，请稍候……");
	doAjax("../service/api",param,{},function(rsMap,op){
		top.layer.close(top.layerIndex);
		if(rsMap.code==0){
			var data = rsMap.data;
			if(data.length>0){
				$("#btn_add").linkbutton('enable');
				$("#btn_delete").linkbutton('enable');
			}
			$("#dg").datagrid("loadData",data);
		}
	});
}

var layerindex = 0;
//弹出评价指标条款表单窗口
function addPjzbData(){
	var zbid = $("#zbid").val();
	if(zbid==""){
		layer.alert("请从左侧选择一个评价指标！");
		return;
	}
	$("#pjtk").textbox('setValue','');
	$("#pf").textbox('setValue','');
	$("#qz1").textbox('setValue','');
	$("#qz2").textbox('setValue','');
	showOpenWin();
}
//弹出表单窗口
function showOpenWin(){
	layerindex = layer.open({
		type: 1,
		title:'评价指标条款及评分信息录入',
		skin: 'layui-layer-rim', //加上边框
		area: ['600px', '360px'], //宽高
		content: $("#divform")
	});
}
//修改评价指标
function editPjzbData(rowIndex){
	setTimeout(function(){
		var selectRow = $("#dg").datagrid("getSelected");
		console.log(selectRow);
		$("#pjzbdataid").val(selectRow.id);
		$("#method").val("editPjzbData");
		$("#pjtk").textbox('setValue',selectRow.pjtk);
		$("#pf").textbox('setValue',selectRow.pf);
		$("#qz1").textbox('setValue',selectRow.qz1);
		$("#qz2").textbox('setValue',selectRow.qz2);
		showOpenWin();
	},100);
}
//添加或者修改评价指标数据
function doSubmitAction(){
	$("#dataform").form('submit',{
		url:"../service/api",
		onSubmit: function(){
			var isValid = $(this).form('validate');
			if(isValid){
				layer.msg("数据处理中，请稍候...",{icon:16,shade:[0.5,'#333333'],time:10000});
				$("#btnSave").linkbutton("disable");
				setTimeout(function(){
					$("#btnSave").linkbutton("enable");
				},5000);
			}else{
				layer.msg('请将表单按要求填写完整！',{icon:0});
			}
			return isValid;// 返回false终止表单提交
		},
		success: function(jsonstr){
			var json = eval('(' + jsonstr + ')');  
			if(json.code==0){
				var data = json.data;
				if(parseInt(data,10)==1){
					layer.close(layerindex);
					loadPjzbData();
					layer.alert("数据保存成功！",{icon:6});
				}else{
					layer.alert("数据保存失败！",{icon:5});
				}
			}else{
				layer.alert("数据保存失败！"+json.msg,{icon:5});
			}
		}
	});
}
//批量删除数据
function betchDeleteData(){
	var rows = $("#dg").datagrid('getSelections');
	if(rows.length==0){
		layer.alert('请从下面列表中选择你要删除的数据！');
		return;
	}
	var ids = [];
	for(var i=0;i<rows.length;i++){
		ids.push(rows[i].id);
	}
	layer.confirm("确定要删除从下面列表中选择的【"+ids.length+"】条数据吗？", function(index){
		layer.close(index);
		var param = new Object();
		param.action="saveOrUpdate";
		param.method="deletePjzbData";
		param.ids = ids.join(",");
		doAjax("../service/api",param,{},function(rsMap,op){
			var data = rsMap.data;
			if(data>0){
				loadPjzbData();
				top.layer.alert("数据批量删除成功！",{icon:6});
			}else{
				top.layer.alert("数据批量删除失败！",{icon:5});
			}
		});
	});
}
