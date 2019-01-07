var op = getPageUrlParam("op");
var selectRecord = null;
if(op=="edit"){
	selectRecord = JSON.parse(localStorage.getItem("wzwj-selected"));
	console.log(selectRecord);
	$("#action").val("updateWzwj");
}else{
	$("#action").val("saveWzwj");
}

$(function(){
	var today = dateUtil.nowDate2String("yyyy-MM-dd");
	$("#wzwjrq").datebox("setValue",today);
	if(selectRecord!=null){
		$("#wzwjrq").textbox("setValue",selectRecord.wzwjrq);
		$("#wznr").textbox("setValue",selectRecord.wznr);
		initFxrDatagrid();
	}
	loadDeptData(initCheJianList);
	initDicCombobox();
});

//数据字典加载完成回调事件
function initDicCombobox(){
	initCombobox("wzlb",getDicData("WZLB",false),"val","text",function(){
		if(selectRecord!=null){
			$("#wzlb").combobox('setValue',selectRecord.wzlb);
		}
	});
	initCombobox("wzxz",getDicData("WZXZ",false),"val","text",function(){
		if(selectRecord!=null){
			$("#wzxz").combobox('setValue',selectRecord.wzxz);
		}
	});
	initCombobox("dnw",getDicData("DNW",false),"val","text",function(){
		if(selectRecord!=null){
			$("#dnw").combobox('setValue',selectRecord.dnw);
		}
	});
	initCombobox("yjbt",getDicData("YJBT",false),"val","text",function(){
		if(selectRecord!=null){
			$("#yjbt").combobox('setValue',selectRecord.yjbt);
		}
	});
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
//初始化单位目录树
function initDeptTree(data){
	cache["treeData"] = data;
	var treeData = getTreeChildren("0");
	console.log(treeData);
	$("#dept").combotree({
		onChange:function(newValue, oldValue){
			loadDeptPerson(newValue);
		}
	}).combotree("loadData",treeData);
}
//加载单位下的人员
function loadDeptPerson(deptid){
	var param = new Object();
	param.action = "loaddata";
	param.method="queryDeptPerson";
	param.deptid = deptid;
	doAjax("service/api",param,{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			initCombobox("person",data,"id","name",function(){});
		}
	});
}
//初始化车间下拉列表
function initCheJianList(data){
	initDeptTree(data);
	var chejian = loadCheJianList(false);
	initCombobox("cj",chejian,"val","text",chejianChangeEvent);
}

//车间切换事件，获取车间工区信息
function chejianChangeEvent(newCjValue,oldCjValue){
	var chejianId = getCheJianIdByName(newCjValue);
	if(selectRecord!=null){
		$("#cj").combobox("setValue",selectRecord.zrcj);
		chejianId = getCheJianIdByName(selectRecord.zrcj);
	}
	var gq_data = loadCheJianGongQu(chejianId);
	initCombobox("gq",gq_data,"val","text",function(newGqValue,oldGqValue){
		if(selectRecord != null){
			$("#gq").combobox('setValue',selectRecord.zrgq);
			loadFhyData(newCjValue,selectRecord.zrgq);
		}else{
			loadFhyData(newCjValue,newGqValue);
		}
	});
}
//根据车间和工区加载防护员
function loadFhyData(cj,gq){
	var param = new Object();
	param.action = "loaddata";
	//param.method="loadCjGqFhy";
	param.method="loadCjGqPerson";
	param.cj = cj;
	param.gq = gq;
	doAjax("service/api",param,{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			console.log(data);
			$("#zyzrr").combobox({
				valueField:"rybs",//valueField:"bh",
				textField:"xm",
				multiple:true,
				editable:false,
				formatter:function(row){
					return row.xm+"（职名："+row.zm+"，年龄："+row.age+"，政治面貌："+row.zzmm+"）";
				},
				onShowPanel:onShowComboPanel
			});
			$("#zyzrr").combobox("loadData",data);
			if(data.length>0){
				if(selectRecord != null){
					var tArr = selectRecord.zyzrr.split(",");
					var values = [];
					for(var i=0;i<data.length;i++){
						if($.inArray(data[i].bh,tArr) != -1){
							values.push(data[i].rybs);
						}
					}
					console.log(values);
					$('#zyzrr').combobox('setValues', values);
				}else{
					$("#zyzrr").combobox("setValue",data[0].rybs);
				}
			}
		}
	});
}
//初始化表单数据
function initFxrDatagrid(){
	if(selectRecord!=null){
		var fxrinfoArr = selectRecord.fxrinfo.split(",");
		var data = [];
		var tArr = [];
		var record = null;
		for(var i=0;i<fxrinfoArr.length;i++){
			tArr = fxrinfoArr[i].split("_");
			record = {};
			record.deptid = tArr[0];
			record.deptname = tArr[1];
			record.personxm = tArr[3];
			record.personid = tArr[2];
			record.op = '<a class="easyui-linkbutton" onclick="removePerson(\''+tArr[2]+'\')">删除</a>';
			data.push(record);
		}
		console.log(data);
		$("#dg").datagrid("loadData",data);
		setFxrFormData();
	}
}

/**
function init(){
	setTimeout(function(){
		initJiaFenDataGrid();
	},300);
}
function initJiaFenDataGrid(){
	$("#dg-jf").datagrid({
		border:false,
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		//nowrap:false,
		fit:true,
		remoteSort:false,
		loadMsg:"正在加载数据，请稍候……",
		columns:[[    	       
			{field:'jf',title:'加分',width:'80'},
			{field:'rq',title:'日期',width:'130'},
			{field:'zz',title:'驻站',width:'80'},
			{field:'xc',title:'现场',width:'80'},
			{field:'yd',title:'远端',width:'80'},
			{field:'fxyd',title:'反向远端',width:'90'},
			{field:'zzdgsj',title:'驻站到岗时间',width:'150'},
			{field:'zzlgsj',title:'驻站离岗时间',width:'150'},
			{field:'sjrwsj',title:'实际入网时间',width:'150'},
			{field:'sjcwsj',title:'实际出网时间',width:'150'}
		]]
	});
}

function initKouFenDataGrid(){
	$("#dg-kf").datagrid({
		border:false,
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		//nowrap:false,
		fit:true,
		remoteSort:false,
		loadMsg:"正在加载数据，请稍候……",
		columns:[[    	       
			{field:'kf',title:'扣分',width:'80'},
			{field:'zz',title:'日期',width:'130'},
			{field:'rq',title:'主要责任人',width:'90'},
			{field:'xc',title:'发现单位',width:'130'},
			{field:'yd',title:'违章类别',width:'90'},
			{field:'fxyd',title:'违章性质',width:'90'},
			{field:'zzdgsj',title:'违章内容',width:'150'},
			{field:'zzlgsj',title:'其他责任人',width:'110'}
		]]
	});
}
*/
//添加发现人
function addFxr(){
	var deptid = $("#dept").combotree("getValue");
	var deptname = $("#dept").combotree("getText");
	var personid = $("#person").combotree("getValue");
	var personxm = $("#person").combotree("getText");
	if(personid==""){
		layer.msg("请选择人员列表中选择发现人！");
	}
	var rows = $("#dg").datagrid("getRows");
	var isExist = false;
	for(var i=0;i<rows.length;i++){
		if(rows[i].personid==personid){
			isExist = true;
			break;
		}
	}
	if(isExist){
		layer.msg("该发现人已存在，请不要重复添加！");
		return;
	}
	$('#dg').datagrid('insertRow',{
		row: {
			deptid:deptid,
			deptname:deptname,
			personid:personid,
			personxm:personxm,
			op:'<a class="easyui-linkbutton" onclick="removePerson('+personid+')">删除</a>'
		}
	});
	setFxrFormData();
	/**
	var rows = $("#dg").datagrid("getRows");
	if(rows.length>0){
		$("#fxdw").val(rows[0].deptid);
		$("#fxr").val(rows[0].personid);
		var fxrArr = [];
		var row = null;
		for(var i=0;i<rows.length;i++){
			row = rows[i];
			fxrArr.push(row.deptid+","+row.personid);
		}
		$("#fxrlist").val(fxrArr.join("|"));
	}
	*/
}
//删除发现人
function removePerson(personid){
	var rows = $("#dg").datagrid("getRows");
	var rowIndex = 0;
	for(var i=0;i<rows.length;i++){
		if(rows[i].personid==personid){
			rowIndex = i;
			break;
		}
	}
	$("#dg").datagrid("deleteRow",rowIndex);
	setFxrFormData();
	
}
//设置发现人表单信息
function setFxrFormData(){
	var rows = $("#dg").datagrid("getRows");
	if(rows.length>0){
		$("#fxdw").val(rows[0].deptid);
		$("#fxr").val(rows[0].personid);
		var fxrArr = [];
		var row = null;
		for(var i=0;i<rows.length;i++){
			row = rows[i];
			fxrArr.push(row.deptid+","+row.personid);
		}
		$("#fxrlist").val(fxrArr.join("|"));
	}
}

//保存数据
function saveData(){
	var data = $("#myform").serializeArray();
	var param = {};
	var key = null;
	var val = null;
	for(var i=0;i<data.length;i++){
		key = data[i].name;
		val = data[i].value;
		if(key in param){
			param[key] = param[key]+","+val;
		}else{
			param[key] = val;
		}
	}
	var isValid = $("#myform").form("validate");
	if(!isValid){
		layer.msg("表单必填项不能为空！");
		return;
	}
	if(param.fxrlist==""){
		layer.msg("请选择发现人及单位并添加到列表中！");
		return;
	}
	var zyzrr = param.zyzrr;
	var tArr = zyzrr.split(",");
	var zrrArr = [];
	for(var i=0;i<tArr.length;i++){
		zrrArr.push(tArr[i].split("_")[1]);
	}
	param.zyzrrinfo = zyzrr;
	param.zyzrr = zrrArr.join(",");
	if(op=="edit"){
		param.wzwjbh = selectRecord.wzwjbh;
	}
	layer.msg("数据处理中，请稍候...",{icon:16,shade:[0.5,'#333333'],time:10000});
	$("#btnSave").linkbutton("disable");
	setTimeout(function(){
		$("#btnSave").linkbutton("enable");
	},5000);
	doAjax("service/api",param,{},function(rsMap,op){
		if(rsMap.code==0){
			layer.alert("数据保存成功！",{icon:6},function(){
				parent.closeInputTab();
			});
		}else{
			layer.alert("数据保存失败！",{icon:5});
		}
	});
	return;
}