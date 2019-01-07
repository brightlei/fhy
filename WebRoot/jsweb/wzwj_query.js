//初始化文件上传控件
var layer = null;
layui.use('layer', function() {
	layer = layui.layer;
});

$(function(){
	initDateBox();
	initQueryDataGrid();
	loadDeptData(initCheJianList);
	initDicCombobox();
});

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
	initCombobox("zzmm",getDicData("ZZMM",true),"val","text",function(){});
	initCombobox("zm",getDicData("ZM",true),"val","text",function(){});
}

//导出违章违纪数据
function exportData(){
	var layerIndex = layer.msg("正在导出数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
	$("#myform").form('submit',{
		url:"service/api?action=exportWzwj&method=queryWzwjByCondition",
		onSubmit:function(param){
			
		},
		success:function(jsonstr){
			layer.close(layerIndex);
	        var json = eval("("+jsonstr+")");
	        if(json.code==0){
	        	var filepath = json.data;
    			$("#downloadFrame").attr("src","servlet/download?filepath="+filepath);
	        }
	    }
	});
}

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
			{field:'fxdwmc',title:'发现单位',width:'100'},
			{field:'fxrxm',title:'发现人',width:'100'},
			{field:'wzlb',title:'违章类别',width:'120'},
			{field:'wzxz',title:'违章性质',width:'120'},
			{field:'wznr',title:'违章内容',width:'140'},
			{field:'zrcj',title:'责任车间',width:'100'},
			{field:'zrgq',title:'责任工区',width:'110'},
			{field:'wzwjfhy',title:'主要责任人',width:'110'},
			{field:'age',title:'年龄',width:'60',
				formatter:function(value,row,index){
					var tArr = row.wzwjfhyinfo.split(",");
					var fxrinfoArr = tArr[0].split("_");
					return fxrinfoArr[2];
			}},
			{field:'zzmm',title:'政治面貌',width:'80',
				formatter:function(value,row,index){
					var tArr = row.wzwjfhyinfo.split(",");
					var fxrinfoArr = tArr[0].split("_");
					return fxrinfoArr[3];
			}},
			{field:'gw',title:'岗位',width:'80',
				formatter:function(value,row,index){
					var tArr = row.wzwjfhyinfo.split(",");
					var fxrinfoArr = tArr[0].split("_");
					return fxrinfoArr[4];
			}},
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
		},
		success:function(jsonstr){    
	        var json = eval("("+jsonstr+")");
	        if(json.code==0){
	        	var data = json.data;
	        	$('#dg-cx').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',data);
	        	/**
	        	var newdata = [];
	        	var bhArr = [];
	        	for(var i=0;i<data.length;i++){
	        		if($.inArray(data[i].wzwjbh,bhArr)==-1){
	        			newdata.push(data[i]);
	        			bhArr.push(data[i].wzwjbh);
	        		}
	        	}
	        	$('#dg-cx').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',newdata);
	        	*/
	        }
	    }
	});
}

function resetCondition(){
	$("#myform").form("reset");
}
