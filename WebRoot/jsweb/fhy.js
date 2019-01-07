$(function(){
	initDataGrid();
	loadDeptData(initCheJianList);
	initDicCombobox();
});

function initDicCombobox(){
	initCombobox("zzmm",getDicData("ZZMM",true),"val","text",function(){});
}

function initCheJianList(data){
	var chejian = loadCheJianList(true);
	initCombobox("cj",chejian,"val","text",chejianChangeEvent);
}

//车间切换事件，获取车间工区信息
function chejianChangeEvent(newValue,oldValue){
	var chejianId = getCheJianIdByName(newValue);
	var gq_data = loadCheJianGongQu(chejianId,true);
	initCombobox("gq",gq_data,"val","text",function(){});
}

//初始化文件上传控件
layui.use('upload', function() {
	var upload = layui.upload;
	//执行实例
	var uploadInst = upload.render({
		elem : '#btn_upload' //绑定元素
		,url : 'service/api?action=importfhy' //上传服务接口
		,accept : 'file'
		,ext:'xls|XLS',
		data : {},
		done : function(result) {//上传完毕回调 200
			var code = result.code;
			if(code==0){
				top.layer.msg("成功导入["+result.data+"]条数据！");
				$("#dg").datagrid('reload');
			}else{
				top.layer.msg("批量导入数据失败！"+result.msg);
			}
		},
		error : function() {//请求异常回调
			top.layer.msg("批量导入数据出现异常！");
		}
	});
});

//初始化表格数据
function initDataGrid(){
	$("#dg").datagrid({
		//url:"service/api?action=getfhy",
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
		toolbar:"#tools",
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'xm',title:'姓名',width:'80'},
			{field:'cj',title:'车间',width:'110'},
			{field:'gq',title:'工区',width:'120'},
			{field:'xb',title:'性别',width:'64'},
			{field:'age',title:'年龄',width:'64'},
			{field:'sjh',title:'手机号码',width:'110'},
			{field:'zzmm',title:'政治面貌',width:'80'},
			{field:'dqfhzg',title:'当前防护资格',width:'120'},
			{field:'cjpxsj',title:'参加培训时间',width:'130'}
		]]
	});
	searchByKey();
}

var isFirst = true;
//根据关键字查询数据
function searchByKey(){
	var key = $("#key").textbox("getValue");
	var param = {action:"loaddata",method:"queryFhyByKey",key:key};
	$("#dg").datagrid("loading");
	doAjax("service/api",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.code==0){
			var data = rsMap.data;
			$('#dg').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',data);
		}
	});
}
//显示高级查询条件面板
function showCondition(type){
	if(type=="show"){
		$("#querybox").show();
	}else{
		$("#querybox").hide();
		searchByKey();
	}
}

//高级查询数据
function searchByCondition(){
	$("#myform").form('submit',{
		url:"service/api?action=loaddata&method=queryFhyByCondition",
		onSubmit:function(param){
			var minage = $("#minage").numberbox("getValue");
			var maxage = $("#maxage").numberbox("getValue");
			var condition = " age bwtween 16 and 100 ";
			if(minage==""){
				minage = 18;
			}
			if(maxage==""){
				maxage = 100;
			}
			param.condition = " age between "+minage+" and "+maxage+" ";
		},
		success:function(jsonstr){    
	        var json = eval("("+jsonstr+")");
	        if(json.code==0){
	        	var data = json.data;
	        	$('#dg').datagrid({pageNumber:1,loadFilter:pagerFilter}).datagrid('loadData',data);
	        }
	    }
	});
}
//数值变化
function numberChange(newValue,oldValue){
	var id = $(this).attr("id");
	if(id=="minage"){
		if(newValue!=""){
			$("#maxage").numberbox({
				min:parseInt(newValue),
				max:100
			});
		}
	}
}