//所有人员信息
var allperson = [];

$(function(){
	var jsonstr = localStorage.getItem("allperson");
	if(jsonstr==null){
		loadAllPerson(initFhyScore);
	}else{
		loadAllPerson(null);
		initFhyScore();
	}
	//initJiaFenDataGrid();
	//initKouFenDataGrid();
});
//人员工号
var bh = getPageUrlParam("bh");
//人员姓名
var xm = getPageUrlParam("xm");
xm = decodeURI(xm);

function loadAllPerson(callback){
	var url = "service/api?action=loaddata&method=getAllPerson";
	doAjax(url,{},{},function(json){
		var data = json.data;
		localStorage.setItem("allperson",JSON.stringify(data));
		if(callback!=null && typeof(callback)=='function'){
			callback();
			console.log(111111);
		}else{
			console.log(222222);
		}
		//initJiaFenDataGrid();
		//initKouFenDataGrid();
	});
}

//初始化防护员积分数据
function initFhyScore(){
	initJiaFenDataGrid();
	initKouFenDataGrid();
}
//获取加分数据
function initJiaFenDataGrid(){
	$("#dg-jf").datagrid({
		url:"service/api?action=loaddata&method=getJiaFenRecord&bh="+bh+"&xm="+xm,
		border:false,
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		fit:true,
		remoteSort:false,
		loadMsg:"正在加载数据，请稍候……",
		loadFilter:function(json){
			var customData = {
				total:0,
				rows:[]
			};
			if(json.code==0){
				var data = json.data;
				customData.total = data.length;
				customData.rows = data;
			}
			return customData;
		},
		columns:[[    	       
			{field:'score',title:'加分',width:'60',align:'center'},
			{field:'zyrq',title:'日期',width:'100',align:'center'},
			{field:'zz',title:'驻站',width:'80',align:'center',formatter:showPerson},
			{field:'xc',title:'现场',width:'80',align:'center',formatter:showPerson},
			{field:'yd',title:'远端',width:'80',align:'center',formatter:showPerson},
			{field:'fxyd',title:'反向远端',width:'90',align:'center',formatter:showPerson},
			{field:'zzdgsj',title:'驻站到岗时间',width:'120',align:'center'},
			{field:'zzlgsj',title:'驻站离岗时间',width:'120',align:'center'},
			{field:'sjrwsj',title:'实际入网时间',width:'120',align:'center'},
			{field:'sjcwsj',title:'实际出网时间',width:'120',align:'center'}
		]],
		onLoadSuccess:function(data){
			var rows = data.rows;
			var total = 0;
			for(var i=0;i<rows.length;i++){
				total += parseFloat(rows[i].score);
			}
			$("#jftotal").text(total);
			var kftotal = parseFloat($("#kftotal").text());
			var bydf = total - kftotal;
			$("#bydf").text(bydf);
		}
	});
}
//获取扣分数据
function initKouFenDataGrid(){
	$("#dg-kf").datagrid({
		url:"service/api?action=loaddata&method=getKouFenRecord&bh="+bh+"&xm="+xm,
		border:false,
		rownumbers:true,
		singleSelect:true,
		autoRowHeight:false,
		striped:true,
		//nowrap:false,
		fit:true,
		remoteSort:false,
		loadMsg:"正在加载数据，请稍候……",
		loadFilter:function(json){
			var customData = {
				total:0,
				rows:[]
			};
			if(json.code==0){
				var data = json.data;
				customData.total = data.length;
				customData.rows = data;
			}
			return customData;
		},
		columns:[[    	       
			{field:'score',title:'扣分',width:'60',align:'center'},
			{field:'wzwjrq',title:'日期',width:'100',align:'center'},
			{field:'zyzrr',title:'主要责任人',width:'160',align:'center',formatter:showPerson},
			{field:'fxdwmc',title:'发现单位',width:'130'},
			{field:'wzlb',title:'违章类别',width:'150'},
			{field:'wzxz',title:'违章性质',width:'190'},
			{field:'wznr',title:'违章内容',width:'250'}
		]],
		onLoadSuccess:function(data){
			var rows = data.rows;
			var total = 0;
			for(var i=0;i<rows.length;i++){
				total += parseFloat(rows[i].score);
			}
			$("#kftotal").text(total);
			var jftotal = parseFloat($("#jftotal").text());
			var bydf = jftotal - total;
			$("#bydf").text(bydf);
		}
	});
}

function showPerson(value,index,row){
	var bhArr = value.split(",");
	var jsonstr = localStorage.getItem("allperson");
	var xmArr = [];
	if(jsonstr!=null){
		var allperson = JSON.parse(jsonstr);
		var count = allperson.length;
		var person = null;
		for(var i=0;i<count;i++){
			person = allperson[i];
			if($.inArray(person.bh,bhArr)!=-1){
				xmArr.push(person.xm);
			}
		}
	}
	return xmArr.join(",");
}