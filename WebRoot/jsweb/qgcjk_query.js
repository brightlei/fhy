var layer = null;
layui.use('layer', function() {
	layer = layui.layer;
});
$(function(){
	var today = dateUtil.nowDate2String("yyyy-MM-dd");
	var yestoday = dateUtil.date2String(dateUtil.calculateByDate(today+" 00:00:00",-7));
	$("#ksrq").datebox('setValue',yestoday);
	$("#jsrq").datebox('setValue',today);
	loadDeptData(initCheJianList);
	initQueryDataGrid();
});

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

//导出全过程监控数据
function exportData(){
	var layerIndex = layer.msg("正在导出数据，请稍候...",{icon:16,shade:[0.5,'#f5f5f5'],scrollbar:false,time:30000});
	$("#myform").form('submit',{
		url:"service/api?action=exportQgcjk&method=queryQgcjkByCondition",
		onSubmit:function(param){
			
		},
		success:function(jsonstr){
			layer.close(layerIndex);
	        var json = eval("("+jsonstr+")");
	        if(json.code==0){
	        	var data = json.data;
	        	$("#downloadFrame").attr("src","servlet/download?filepath="+data);
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
		toolbar:"#cxtool",
		loadMsg:"正在加载数据，请稍候……",
		columns:[[
			{field:'sjzt',title:'数据状态',width:'70',align:'center',
				formatter:function(value,row,index){
			    	if(value=="1"){
			    		return '<span style="color:red;font-weight:bold;">待填报</span>';
			    	}else{
			    		return '<span style="color:blue;font-weight:bold;">已填报</span>';
			    	}
			}},
			{field:'zyrq',title:'日期',width:'86',align:'center'},
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
		]],
		onLoadSuccess:function(data){
//			$("a[field=tips]").tooltip({
//				onShow: function(){
//                    $(this).tooltip('tip').css({
//                        width:'400',
//                        boxShadow: '1px 1px 3px #292929',
//                        wordWrap:"break-word"
//                    });
//                }
//			});
		},
		onDblClickCell:function(index, field, value){
			layer.open({
			  type: 1,
			  shade: false,
			  area:["600px","auto"],
			  title: false,//不显示标题
			  content: value
			});
		}
	});
	searchByCondition();
}

//根据条件查询数据
function searchByCondition(){
	$("#myform").form('submit',{
		url:"service/api?action=loaddata&method=queryQgcjkByCondition",
		onSubmit:function(param){
			$("#dg-cx").datagrid("loading");
		},
		success:function(jsonstr){
			$("#dg-cx").datagrid("loaded");
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
