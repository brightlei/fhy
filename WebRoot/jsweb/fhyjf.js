$(function(){
	initYearList();
	initMonth();
	loadDeptData(initCheJianCombobox);
	initDicCombobox();
	initDataGrid();
});

//初始化年份
function initYearList(){
	var year = new Date().getFullYear();
	var newdata = [];
	newdata.push({name:year});
	newdata.push({name:year-1});
	newdata.push({name:year-2});
	initCombobox("year",newdata,"name","name",function(){});
}
//初始化月份
function initMonth(){
	var month = new Date().getMonth() + 1;
	var monthstr = month<10?"0"+month:""+month;
	$("#month").combobox("setValue",monthstr);
}

//初始化数据字典
function initDicCombobox(){
	var zzmm_data = getDicData("ZZMM",true);
	initCombobox("zzmm",zzmm_data,"val","text",function(){});
}
//初始化车间数据列表
function initCheJianCombobox(){
	var chejian = loadCheJianList(true);
	initCombobox("cj",chejian,"val","text",chejianChangeEvent);
	chejianChangeEvent("","");
}
//车间选择切换事件，加载车间工区
function chejianChangeEvent(newValue, oldValue){
	if(newValue==""){
		var gongqu_data = [{id:0,val:"",text:"全部"}];
		initCombobox("gq",gongqu_data,"val","text",function(){});
		return;
	}
	var chejianId = getCheJianIdByName(newValue);
	var gongqu_data = loadCheJianGongQu(chejianId,true);
	initCombobox("gq",gongqu_data,"val","text",function(){});
}

//初始化防护员积分数据表格
function initDataGrid(){
	$("#dg").datagrid({
		border:false,
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		//nowrap:false,
		fit:true,
		//pagination:true,
		//remoteSort:false,
		//toolbar:"#tools",
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
		    {field:'bh',title:'工号',width:'110'},
			{field:'xm',title:'姓名',width:'80',
			formatter:function(value,row,index){
				return '<a href="javascript:void(0)" onclick="showScore(this)" bh="'+row.bh+'" style="font-weight:bold;color:blue;">'+value+'</a>';
			}
			},
			{field:'cj',title:'车间',width:'110'},
			{field:'gq',title:'工区',width:'120'},
			{field:'xb',title:'性别',width:'50'},
			{field:'age',title:'年龄',width:'50'},
			{field:'sjh',title:'手机号码',width:'110'},
			{field:'zzmm',title:'政治面貌',width:'80'},
			{field:'dqfhzg',title:'当前防护资格',width:'120'},
			{field:'cjpxsj',title:'参加培训时间',width:'100',align:'center'},
			{field:'bydf',title:'本月得分',width:'80',align:'center',
				formatter:function(value,row,index){
					if(value==null || value==""){
						value = 0;
					}
					return value;
				}},
			{field:'ljjf',title:'累计得分',width:'80',align:'center',
				formatter:function(value,row,index){
					if(value==null || value==""){
						value = 0;
					}
					return value;
				}},
			{field:'jl',title:'当月奖励(元)',width:'96'}
		]]
	});
	queryData();
}

//加载防护员本月积分数据
function queryData(){
	$('#myform').form('submit', {    
	    url:basepath+'service/api',    
	    onSubmit: function(){    
	    	var isValid = $(this).form('validate');
			if(isValid){
				$("#dg").datagrid("loading");
				$("#btnQuery").linkbutton("disable");
				setTimeout(function(){
					$("#btnQuery").linkbutton("enable");
				},3000);
			}
			return isValid;// 返回false终止表单提交
	    },    
	    success:function(jsonstr){
	    	$("#dg").datagrid("loaded");
			var json = eval('(' + jsonstr + ')');
			if(json.code==0){
				var data = json.data;
				$("#dg").datagrid("loadData",data);
			}
	    }    
	});  
	/**
	 * var year = $("#year").combobox("getValue"); var month =
	 * $("#month").combobox("getValue"); var param =
	 * {action:"loaddata",method:"queryFhyScoreMoney",year:year,month:month};
	 * $("#dg").datagrid("loading");
	 * doAjax(basepath+"service/api",param,{},function(rsMap,op){
	 * $("#dg").datagrid("loaded"); if(rsMap.code==0){ var data = rsMap.data;
	 * cache["fhyscore"]=data; $("#dg").datagrid("loadData",data); } });
	 */
}

function showScore(obj){
	var bh = $(obj).attr("bh");
	var xm = $(obj).text();
	parent.addTab("jfdetails","["+xm+"]积分详情","jftj.html?bh="+bh+"&xm="+xm);
}
