//初始化文件上传控件
var layer = null;
layui.use('layer', function() {
	layer = layui.layer;
});

$(function(){
	//initDateBox();
	initDataGrid();
	//initQueryDataGrid();
	$("#mytab").tabs({
		onBeforeClose:function(title,index){
			initDataGrid();
		}
	});
	//loadDeptData(initCheJianList);
	//initDicCombobox();
});

//新加一个tab页进行数据录入
function openWin(){
	var name = "datainput";
	var title = "违章违纪数据录入";
	var pagesrc = "wzwjform.html?t="+Math.random();
	var content = '<iframe id="'+name+'" name="'+name+'" src="'+pagesrc+'" frameborder="0" scrolling="0" style="width:100%;height:100%;"></iframe>';
	$('#mytab').tabs('add',{
	    title:title,
	    content:content,
	    closable:true
	});
	$("#"+name).parent().css("overflow","hidden");
}

/**
function selectDate(date){
	var id = $(this).attr("id");
	var ksrq = $("#ksrq").datebox("getValue");
	var jsrq = $("#jsrq").datebox("getValue");
	var temp = ksrq;
	if(id=="ksrq"){
		ksrq = dateUtil.date2String("yyyy-MM-dd",date);
		temp = jsrq;
	}else if(id=="jsrq"){
		jsrq = dateUtil.date2String("yyyy-MM-dd",date);
		temp = ksrq;
	}
	if(jsrq<ksrq){
		parent.layer.msg("开始日期不能超过结束日期！");
		$("#btn_submit").linkbutton("disable");
	}else{
		$("#btn_submit").linkbutton("enable");
	}
}
//初始化查询日期
function initDateBox(){
	var nowtime = dateUtil.nowDate2String("yyyy-MM-dd");
	var yearmonth = nowtime.substring(0,7);
	var monthfirstday = yearmonth+"-01";
	$("#ksrq").datebox('setValue',monthfirstday);
	$("#jsrq").datebox('setValue',nowtime);
}
//初始化车间数据
function initCheJianList(data){
	initDeptTree(data);
	var chejian = loadCheJianList(true);
	initCombobox("cj",chejian,"val","text",chejianChangeEvent);
}

//车间切换事件，获取车间工区信息
function chejianChangeEvent(newValue,oldValue){
	var chejianId = getCheJianIdByName(newValue);
	var gq_data = loadCheJianGongQu(chejianId,true);
	initCombobox("gq",gq_data,"val","text",function(){});
}
//初始化单位目录树
function initDeptTree(data){
	cache["treeData"] = data;
	var treeData = getTreeChildren("0");
	console.log(treeData);
	$("#fxdw").combotree("loadData",treeData);
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
			treeNode.code = record.code;
			treeNode.text = record.name;
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
	return treeArr;
}

function initDicCombobox(){
	initCombobox("wzlb",getDicData("WZLB",true),"val","text",function(){});
	initCombobox("wzxz",getDicData("WZXZ",true),"val","text",function(){});
	initCombobox("dnw",getDicData("DNW",true),"val","text",function(){});
	initCombobox("yjbt",getDicData("YJBT",true),"val","text",function(){});
}
*/
function initDataGrid(){
	$("#dg").datagrid({
		//url:"service/api?action=pagedata&method=loadUserWzwj",
		border:false,
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		//nowrap:false,
		fit:true,
		pagination:true,
		pageNumber:1,
		remoteSort:false,
		toolbar:"#dgtool",
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'op',title:'操作',width:'60',align:'center',
			    formatter:function(value,row,index){
			    	if(row.timespace>1){
			    		return '<label style="color:blue;font-weight:bold;">已归档</label>';
			    	}else{
			    		return '<button class="layui-btn layui-btn-danger layui-btn-xs" onclick="deleteData('+index+')">删除</button>';
			    		//return '<button class="layui-btn layui-btn-normal layui-btn-xs" onclick="editData('+index+')">修改</button>';
			    	}
			}},
			{field:'wzwjrq',title:'日期',width:'90'},
			{field:'fxdwmc',title:'发现单位',width:'110'},
			{field:'fxrxm',title:'发现人',width:'110',
				formatter:function(value,row,index){
					var tArr = row.fxrinfo.split(",");
					var fxrinfoArr = tArr[0].split("_");
					return fxrinfoArr[3];
			}},
			{field:'wzlb',title:'违章类别',width:'110'},
			{field:'wzxz',title:'违章性质',width:'120'},
			{field:'wznr',title:'违章内容',width:'300'},
			{field:'zrcj',title:'责任车间',width:'120'},
			{field:'zrgq',title:'责任工区',width:'110'},
			{field:'wzwjfhy',title:'主要责任人',width:'110'},
			//{field:'zzmm',title:'岗位',width:'80'},
			//{field:'zzmm',title:'年龄',width:'80'},
			//{field:'zzmm',title:'政治面貌',width:'80'},
			{field:'dnw',title:'点内/外',width:'80'},
			{field:'yjbt',title:'夜间/白天',width:'80'},
			{field:'cjsj',title:'创建时间',width:'140'},
			{field:'xgsj',title:'最后修改时间',width:'140'}
		]]
//		loadFilter:function(data){
//			var rows = data.rows;
//			cache["rows"]=rows;
//			return data;
//		}
	});
	loadUserWzwj();
}
//加载用户录入的违章违纪数据
function loadUserWzwj(){
	var param = {action:"loaddata",method:"loadUserWzwj"};
	$("#dg").datagrid("loading");
	doAjax("service/api",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.code==0){
			var data = rsMap.data;
			$('#dg').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',data);
		}
	});
}
/**
 * 修改数据
 * @param index
 */
function editData(index){
	var rows = $("#dg").datagrid("getRows");
	console.log(rows);
	var selectRow = rows[index];
	console.log(selectRow);
	localStorage.setItem("wzwj-selected",JSON.stringify(selectRow));
	var name = "dataedit";
	var title = "违章违纪数据录入";
	var pagesrc = "wzwjform.html?op=edit&t="+Math.random();
	var content = '<iframe id="'+name+'" name="'+name+'" src="'+pagesrc+'" frameborder="0" scrolling="0" style="width:100%;height:100%;"></iframe>';
	$('#mytab').tabs('add',{
	    title:title,
	    content:content,
	    closable:true
	});
	$("#"+name).parent().css("overflow","hidden");
}
//删除数据
function deleteData(){
	layer.confirm('确认要删除该条数据吗？', {icon: 3, title:'提示'}, function(index){
		layer.close(index);
		var selectRow = $("#dg").datagrid("getSelected");
		var rowIndex = $("#dg").datagrid("getRowIndex",selectRow);
		var wzwjbh = selectRow.wzwjbh;
		var param = {action:"deleteWzwj",wzwjbh:wzwjbh,jfbh:wzwjbh};
		doAjax(basepath+"service/api",param,{},function(rsMap,op){
			if(rsMap.code==0){
				var data = rsMap.data;
				if(parseInt(data)>0){
					$("#dg").datagrid("deleteRow",rowIndex);
					layer.alert('数据删除成功！',{icon:6});
				}
			}
		});
	});
}

//根据关键字查询数据
function searchByKey(){
	var key = $("#key").textbox("getValue");
	var param = {action:"loaddata",method:"queryWzwjByKey",key:key};
	$("#dg").datagrid("loading");
	doAjax("service/api",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.code==0){
			var data = rsMap.data;
			$('#dg').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',data);
		}
	});
}

//导出违章违纪数据
function exportData(){
	var key = $("#key").textbox("getValue");
	var param = {action:"exportWzwj",method:"queryWzwjByKey",key:key};
	var layerIndex = layer.msg("正在导出数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
	doAjax("service/api",param,{},function(rsMap,op){
		layer.close(layerIndex);
		if(rsMap.code==0){
			var filepath = rsMap.data;
			$("#downloadFrame").attr("src","servlet/download?filepath="+filepath);
		}
	});
}
//关闭违章违纪数据录入界面
function closeInputTab(){
	$('#mytab').tabs('close','违章违纪数据录入');
}
/**
function initQueryDataGrid(){
	$("#dg-cx").datagrid({
		border:false,
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		//nowrap:false,
		fit:true,
		pagination:true,
		remoteSort:false,
		toolbar:"#dgcxtool",
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'wzwjrq',title:'日期',width:'90'},
			{field:'deptname',title:'发现单位',width:'100'},
			{field:'fxrxm',title:'发现人',width:'60'},
			{field:'wzlb',title:'违章类别',width:'120'},
			{field:'wzxz',title:'违章性质',width:'120'},
			{field:'wznr',title:'违章内容',width:'140'},
			{field:'zrcj',title:'责任车间',width:'100'},
			{field:'zrgq',title:'责任工区',width:'110'},
			{field:'zyzrr',title:'主要责任人',width:'110'},
			{field:'gw',title:'岗位',width:'80'},
			{field:'age',title:'年龄',width:'60'},
			{field:'zzmm',title:'政治面貌',width:'80'},
			{field:'dnw',title:'点内/外',width:'80'},
			{field:'yjbt',title:'夜间/白天',width:'80'}
		]]
	});
	searchByCondition();
}




//高级查询数据
function searchByCondition(){
	$("#myform").form('submit',{
		url:"service/api?action=loaddata&method=queryWzwjByCondition",
		onSubmit:function(param){
//			var minage = $("#minage").numberbox("getValue");
//			var maxage = $("#maxage").numberbox("getValue");
//			var condition = " age bwtween 16 and 100 ";
//			if(minage==""){
//				minage = 18;
//			}
//			if(maxage==""){
//				maxage = 100;
//			}
//			param.condition = " age between "+minage+" and "+maxage+" ";
		},
		success:function(jsonstr){    
	        var json = eval("("+jsonstr+")");
	        if(json.code==0){
	        	var data = json.data;
	        	var newdata = [];
	        	var bhArr = [];
	        	for(var i=0;i<data.length;i++){
	        		if($.inArray(data[i].wzwjbh,bhArr)==-1){
	        			newdata.push(data[i]);
	        			bhArr.push(data[i].wzwjbh);
	        		}
	        	}
	        	$('#dg-cx').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',newdata);
	        }
	    }
	});
}

function resetCondition(){
	$("#myform").form("reset");
}
*/