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
			{field:'jhly',title:'计划来源',width:'100'},
			{field:'zyml',title:'作业命令号',width:'160'},
			{field:'hangb',title:'行别',width:'60'},
			{field:'dnw',title:'点内外',width:'60'},
			{field:'zysd',title:'作业时段',width:'140'},
			{field:'bgdd',title:'报工地点',width:'220'},
			{field:'zyxm',title:'作业项目',width:'220'},
			{field:'phtzs',title:'配合通知书',width:'120'},
			{field:'phry',title:'配合人员',width:'90'},
			{field:'sfyzjhxf',title:'是否与周计划相符',width:'120'},
			{field:'bzxx',title:'备注',width:'120'},
			{field:'dbr',title:'带班人',width:'90'},
			{field:'lwg',title:'劳务工',width:'90'},
			{field:'zgcqrs',title:'职工出勤人数',width:'90'},
			{field:'zgcql',title:'职工出勤率',width:'90'},
			{field:'shyjsl',title:'手机应交数量',width:'90'},
			{field:'shsjsl',title:'手机实交数量',width:'90'},
			{field:'sdrs',title:'上道人数',width:'90'},
			{field:'xdrs',title:'下道人数',width:'90'},
			{field:'sdl',title:'上道率',width:'90'},
			{field:'zz_xm',title:'驻站',width:'90'},
			{field:'xc_xm',title:'现场',width:'90'},
			{field:'yd_xm',title:'远端',width:'90'},
			{field:'fxyd_xm',title:'反向远端',width:'90'},
			{field:'jwmlh',title:'进网命令号',width:'120'},
			{field:'cwmlh',title:'出网命令号',width:'120'},
			{field:'wksbqk',title:'网口锁闭情况',width:'90'},
			{field:'gwjjsyqk',title:'高危机具<br>使用情况',width:'120'},
			{field:'zzdgsj',title:'驻站<br>到岗时间',width:'70'},
			{field:'zzlgsj',title:'驻站<br>离岗时间',width:'70'},
			{field:'sjrwsj',title:'实际<br>入网时间',width:'70'},
			{field:'sjcwsj',title:'实际<br>出网时间',width:'70'},
			{field:'tdtc',title:'图定天窗',width:'120'},
			{field:'tckssj',title:'开窗<br>开始时间',width:'70'},
			{field:'tcsjsj',title:'开窗<br>结束时间',width:'70'},
			{field:'sjzydd',title:'实际<br>作业地点',width:'220'},
			{field:'wcgzl',title:'完成工作量',width:'220'},
			{field:'gwsczpsfsc',title:'轨温三测照片<br>是否上传',width:'90'},
			{field:'jcwjjsfyz',title:'进出网机具<br>是否一致',width:'90'},
			{field:'zbrs',title:'值班人数',width:'90'},
			{field:'czwt',title:'存在问题',width:'120'},
			{field:'clyj',title:'处理意见',width:'120'}
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
			showTableCellInfo(value);
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
