$(function(){
	initDataGrid();
});

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
		toolbar:"#tools",
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
			{field:'xb',title:'性别',width:'64'},
			{field:'age',title:'年龄',width:'64'},
			{field:'sjh',title:'手机号码',width:'110'},
			{field:'zzmm',title:'政治面貌',width:'80'},
			{field:'dqfhzg',title:'当前防护资格',width:'120'},
			{field:'cjpxsj',title:'参加培训时间',width:'100'},
			{field:'bydf',title:'本月得分',width:'80',align:'center',sortable:true,
				formatter:function(value,row,index){
					if(value==null || value==""){
						value = 0;
					}
					return value;
				}},
			{field:'ljjf',title:'累计得分',width:'80',align:'center',sortable:true,
				formatter:function(value,row,index){
					if(value==null || value==""){
						value = 0;
					}
					return value;
				}},
			{field:'jl',title:'奖励(元)',width:'90',align:'center',sortable:true,
				styler:function(value,row,index){
					return 'font-weight:bolder;';
				}}
		]]
	});
	loadData();
}
//加载防护员本月积分数据
function loadData(){
	var year = new Date().getFullYear();
	var month = new Date().getMonth() + 1;
	var monthstr = month<10?"0"+month:""+month;
	var param = {action:"loaddata",method:"getFhyScoreMoney",year:year,month:monthstr};
	$.year = year;
	$.month = monthstr;
	$("#dg").datagrid("loading");
	doAjax(basepath+"service/api",param,{},function(rsMap,op){
		$("#dg").datagrid("loaded");
		if(rsMap.code==0){
			var data = rsMap.data;
			cache["fhyscore"]=data;
			$("#jlrs").numberbox("setValue",data.length);
			$("#dg").datagrid("loadData",data);
		}
	});
}

function showScore(obj){
	var bh = $(obj).attr("bh");
	var xm = $(obj).text();
	parent.addTab("jfdetails","["+xm+"]积分详情","jftj.html?bh="+bh+"&xm="+xm);
}
//奖励分配
function setPersonMoney(){
	var money = $("#jlje").numberbox("getValue");
	var jlrs = $("#jlrs").numberbox("getValue");
	if(money==null || money==""){
		$("#msg").html("请输入奖励金额！");
		return;
	}
	if(jlrs==null || jlrs==""){
		$("#msg").html("请输入奖励人数！");
		return;
	}
	money = parseInt(money,10);
	jlrs = parseInt(jlrs,10);
	var data = cache["fhyscore"];
	var datacount = data.length;
	if(jlrs>datacount){
		$("#msg").html("奖励人数不能超过可奖励人数！");
		return;
	}
	$("#msg").html("");
	layer.confirm("确定将奖励金额【"+money+"元】按积分排名前【"+jlrs+"人】进行奖励分配？",{icon:3},function(index){
		layer.close(index);
		var data = cache["fhyscore"];
		var totalScore = 0;
		var newdata = [];
		var record = null;
		for(var i=0;i<jlrs;i++){
			record = data[i];
			console.log(record);
			totalScore += parseFloat(record.bydf);
			newdata.push(record);
		}
		var useMoney = 0;
		for(var i=0;i<jlrs;i++){
			record = newdata[i];
			var _money = money*(parseFloat(record.bydf)/totalScore);
			if(i==jlrs-1){
				record.jl = parseFloat(money - useMoney).toFixed(2);
			}else{
				record.jl = parseFloat(_money).toFixed(2);
			}
			useMoney+=1*record.jl;
		}
		cache["fhymoney"] = newdata;
		$("#dg").datagrid("loadData",newdata);
		console.log(money+"==="+useMoney);
		layer.msg("奖励分配成功！请确认后奖励分配无误后点击‘保存奖励分配’按钮保存分配结果！",{icon:6,timeout:4});
	});
}
//保存奖励分配结果
function savePersonMoney(){
	var data = cache["fhymoney"];
	var datavalue = [];
	for(var i=0;i<data.length;i++){
		datavalue.push(data[i].bh+","+data[i].jl);
	}
	var param = {action:"savefhyjl",year:$.year,month:$.month,data:datavalue.join("|")};
	$("#saveBtn").linkbutton("disable");
	setTimeout(function(){
		$("#saveBtn").linkbutton("enable");
	})
	doAjax(basepath+"service/api",param,{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			if(parseInt(data)>0){
				layer.msg("奖励分配结果保存成功！");
			}
		}
	});
}

//导出奖励分配结果
function exportPersonMoney(){
	var param = {action:"exportFhyMonthMoney"};
	var layerIndex = layer.msg("正在导出数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
	doAjax("service/api",param,{},function(rsMap,op){
		layer.close(layerIndex);
		if(rsMap.code==0){
			var filepath = rsMap.data;
			$("#downloadFrame").attr("src","servlet/download?filepath="+filepath);
		}
	});
}