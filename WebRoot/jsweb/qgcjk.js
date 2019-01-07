var layer = null;
layui.use('layer', function() {
	layer = layui.layer;
});

//初始化文件上传控件
layui.use('upload', function() {
	var upload = layui.upload;
	//执行实例
	var uploadInst = upload.render({
		elem : '#btn_upload' //绑定元素
		,url : basepath+'service/api?action=importQgcjk' //上传服务接口
		,accept : 'file'
		,ext:'xls|XLS',
		data : {},
		before:function(obj){
			top.layer.msg("正在批量导入数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
		},
		done : function(result) {//上传完毕回调 200
			var code = result.code;
			if(code==0){
				top.layer.alert("成功导入更新["+result.data+"]条数据！",{icon:6});
				searchByKey();
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
	initDataGrid();
	$("#mytab").tabs({
		onBeforeClose:function(title,index){
			initDataGrid();
		}
	});
	$("#btn_delete").linkbutton({
		onClick:function(){
			var text = $(this).text();
			if(text=="批量删除"){
				$("#dg").datagrid("showColumn","ids");
				$(this).linkbutton({text:"执行批量删除"});
			}else{
				batchDeleteData();
				//$("#dg").datagrid({singleSelect:true}).datagrid("hideColumn","ids").datagrid("unselectAll");
				//$(this).linkbutton({text:"批量删除"});
			}
		}
	});
});

function initDataGrid(){
	var key = $("#key").textbox("getValue");
	$("#dg").datagrid({
		border:false,
		url:"service/api?action=pagedata&method=loadQgcjk&key="+key,
		rownumbers:true,
		//singleSelect:true,
		autoRowHeight:false,
		striped:true,
		//nowrap:false,
		fit:true,
		pagination:true,
		pageNumber:1,
		remoteSort:false,
		toolbar:"#dgtool",
		loadMsg:"正在加载数据，请稍候……",
		rowStyler:function(index,row){
			if(row.sjzt==1){
				return 'color:red;';
				//return 'background:#FFFF66;';
			}
		},
		columns:[[
		    {field:'ids',checkbox:true,hidden:true},
			{field:'op',title:'操作',width:'100',align:'center',
			    formatter:function(value,row,index){
			    	return '<button class="layui-btn layui-btn-normal layui-btn-xs" onclick="editData('+index+')">修改</button><button class="layui-btn layui-btn-danger layui-btn-xs" onclick="deleteData('+index+')">删除</button>';
			}},
			{field:'sjzt',title:'数据状态',width:'70',align:'center',
				formatter:function(value,row,index){
			    	if(value=="1"){
			    		return '<span style="color:red;font-weight:bold;">待填报</span>';
			    	}else{
			    		return '<span style="color:blue;font-weight:bold;">已填报</span>';
			    	}
			}},
			{field:'zyrq',title:'日期',width:'86'},
			{field:'cj',title:'车间',width:'110'},
			{field:'gq',title:'工区',width:'120'},
			{field:'jhly',title:'计划来源'},
			{field:'hangb',title:'行别'},
			{field:'dnw',title:'点内外'},
			{field:'bgdd',title:'报工地点',width:'220'},
			{field:'zyxm',title:'作业项目',width:'220'},
			{field:'phtzs',title:'配合通知书'},
			{field:'phry',title:'配合人员'},
			{field:'sfyzjhxf',title:'是否与周计划查符'},
			{field:'bzxx',title:'备注'},
			{field:'dbr',title:'带班人'},
			{field:'lwg',title:'劳务工'},
			{field:'zgcqrs',title:'职工出勤人数'},
			{field:'zgcql',title:'职工出勤率'},
			{field:'shyjsl',title:'手机应交数量'},
			{field:'shsjsl',title:'手机实交数量'},
			{field:'sdrs',title:'上道人数'},
			{field:'xdrs',title:'下道人数'},
			{field:'sdl',title:'上道率'},
			{field:'zz_xm',title:'驻站'},
			{field:'xc_xm',title:'现场'},
			{field:'yd_xm',title:'远端'},
			{field:'fxyd_xm',title:'反向远端'},
			{field:'jwmlh',title:'进网命令号'},
			{field:'cwmlh',title:'出网命令号'},
			{field:'wksbqk',title:'网口锁闭情况'},
			{field:'gwjjsyqk',title:'高危机具使用情况'},
			{field:'zzdgsj',title:'驻站到岗时间'},
			{field:'zzlgsj',title:'驻站离岗时间'},
			{field:'sjrwsj',title:'实际入网时间'},
			{field:'sjcwsj',title:'实际出网时间'},
			{field:'tdtc',title:'图定天窗'},
			{field:'tckssj',title:'开窗开始时间'},
			{field:'tcsjsj',title:'开窗结束时间'},
			{field:'sjzydd',title:'实际作业地点'},
			{field:'wcgzl',title:'完成工作量'},
			{field:'jcwjjsfyz',title:'进出网机具是否一致'},
			{field:'zbrs',title:'值班人数'},
			{field:'czwt',title:'存在问题'},
			{field:'clyj',title:'处理意见'}
		]]
	});
}
//条件模糊查询
function searchByKey(){
	initDataGrid();
}

//导出全过程监控数据
function exportData(){
	var key = $("#key").textbox("getValue");
	var param = {action:"exportQgcjk",method:"loadQgcjk",key:key};
	var layerIndex = layer.msg("正在导出数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
	doAjax("service/api",param,{},function(rsMap,op){
		layer.close(layerIndex);
		if(rsMap.code==0){
			var filepath = rsMap.data;
			$("#downloadFrame").attr("src","servlet/download?filepath="+filepath);
		}
	});
}

var newTabTitle = "";
//打开新的tab页
function openWin(){
	var name = "datainput";
	var title = "全过程监控表填报";
	if(top.qgcjk_selectRow!=null){
		title = "全过程监控表编辑修改";
	}
	newTabTitle = title;
	var pagesrc = "qgcjkform.html?t="+Math.random();
	var content = '<iframe id="'+name+'" name="'+name+'" src="'+pagesrc+'" frameborder="0" scrolling="0" style="width:100%;height:100%;"></iframe>';
	$('#mytab').tabs('add',{
	    title:title,
	    content:content,
	    closable:true
	});
	$("#"+name).parent().css("overflow","hidden");
}
//更新数据
function editData(index){
	setTimeout(function(){
		var selectRow = $("#dg").datagrid("getSelected");
		console.log(selectRow);
		top.qgcjk_selectRow = selectRow;
		openWin();
	},100);
}

//删除数据
function deleteData(){
	layer.confirm('确认要删除该条数据吗？', {icon: 3, title:'提示'}, function(index){
		layer.close(index);
		var selectRow = $("#dg").datagrid("getSelected");
		console.log(selectRow);
		var rowIndex = $("#dg").datagrid("getRowIndex",selectRow);
		var qgcjkbh = selectRow.qgcjkbh;
		var param = {action:"deleteQgcjk",qgcjkbh:qgcjkbh,jfbh:qgcjkbh};
		console.log(param);
		doAjax(basepath+"service/api",param,{},function(rsMap,op){
			if(rsMap.code==0){
				var data = rsMap.data;
				if(parseInt(data)>0){
					layer.msg("数据删除成功！");
					$("#dg").datagrid("deleteRow",rowIndex);
				}
			}
		});
	});
}
//批量删除数据
function batchDeleteData(){
	var rows = $("#dg").datagrid("getSelections");
	if(rows.length==0){
		$("#btn_delete").linkbutton({text:"批量删除"});
		$("#dg").datagrid("hideColumn","ids").datagrid("unselectAll");
		return;
	}
	layer.confirm('确认要删除这些数据吗？', {icon: 3, title:'提示'}, function(index){
		layer.close(index);
		var idArr = [];
		for(var i=0;i<rows.length;i++){
			idArr.push(rows[i].qgcjkbh);
		}
		var param = {action:"batchDeleteQgcjk",ids:idArr.join(",")};
		var layerindex = layer.msg("正在批量删除数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
		doAjax(basepath+"service/api",param,{},function(rsMap,op){
			if(rsMap.code==0){
				var data = rsMap.data;
				if(parseInt(data)>0){
					layer.alert("数据删除成功！共删除["+data+"]条数据！",{icon:6});
					$("#btn_delete").linkbutton({text:"批量删除"});
					$("#dg").datagrid("hideColumn","ids").datagrid("unselectAll");
					searchByKey();
				}
			}
		});
	});
}

//关闭表单操作页面
function closeInputTab(){
	$('#mytab').tabs('close',newTabTitle);
}
