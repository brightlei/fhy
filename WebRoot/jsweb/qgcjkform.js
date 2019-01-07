$(function(){
	//alert(top.qgcjk_selectRow);
	//console.log(top.qgcjk_selectRow);
	//initJiaFenDataGrid();
	//initKouFenDataGrid();
	var today = dateUtil.nowDate2String("yyyy-MM-dd");
	$("#zyrq").datebox("setValue",today);
	$("#opuser").val(user.username);
	initDicCombobox();
	loadDeptData(initCheJianList);
	initEditFormData();
	//initFormFocus();
	//loadDicData(initDicCombobox);
});
function initFormFocus(){
	var elems = $("form *[field]");
	console.log(elems);
    var index = 0;
    elems.eq(index).focus();
    $(document).keyup(function(e){
        var kc = e.keyCode;
        console.log(kc);
        if(kc == 13){
        	e.keyCode=9;
//            index++;
//            index = index > elems.length - 1 ? 0 : index;
//            elems.eq(index).focus();
        }
        return false;
    });
}

//初始化字典项
function initDicCombobox(){
	initCombobox("hangb",getDicData("HANGBIE",false),"val","text",function(){});
	initCombobox("dnw",getDicData("DNW",false),"val","text",function(){});
	initCombobox("jhly",getDicData("JHLY",false),"val","text",function(){});
	initCombobox("wksbqk",getDicData("WKSBQK",false),"val","text",function(){});
}
//初始化车间信息
function initCheJianList(){
	var chejian = loadCheJianList();
	console.log(chejian);
	cache["chejian"] = chejian;
	$("#cj").combobox({
		valueField:"val",
		textField:"text",
		editable:false,
		onShowPanel:onShowComboPanel,
		onChange:function(newValue, oldValue){
			var chejianId = getCheJianIdByName(newValue);
			initCheJianGongQu(chejianId);
		}
	});
	$("#cj").combobox("loadData",chejian);
	if(chejian.length>0){
		if(top.qgcjk_selectRow==null){
			$("#cj").combobox('setValue', chejian[0].val);
		}else{
			$("#cj").combobox('setValue', top.qgcjk_selectRow.cj);
		}
		//var chejianId = getCheJianIdByName(chejian[0].text);
		//initCheJianGongQu(chejianId);
	}
}
//根据车间名称获取车间ID
function getCheJianIdByName(text){
	var data = cache["chejian"];
	var chejianId = 0;
	for(var i=0;i<data.length;i++){
		if(data[i].text==text){
			chejianId = data[i].id;
			break;
		}
	}
	return chejianId;
}

var isFhyInit = true;
//根据车间ID获取工区信息
function initCheJianGongQu(chejianId){
	var data = loadCheJianGongQu(chejianId);
	$("#gq").combobox({
		valueField:"val",
		textField:"text",
		editable:false,
		onShowPanel:onShowComboPanel,
		onChange:function(newValue, oldValue){
			var cjname = $("#cj").combobox('getValue');
			loadFhyData(cjname,newValue);
		}
	});
	$("#gq").combobox("loadData",data);
	if(data.length>0){
		if(top.qgcjk_selectRow==null){
			$("#gq").combobox('setValue', data[0].val);
		}else{
			$("#gq").combobox('setValue', top.qgcjk_selectRow.gq);
		}
	}
}
//加载车间工区下的防护员数据
function loadFhyData(cj,gq){
	var param = new Object();
	param.action = "loaddata";
	param.method="loadCjGqFhy";
	param.cj = cj;
	param.gq = gq;
	doAjax("service/api",param,{},function(rsMap,op){
		if(rsMap.code==0){
			var data = rsMap.data;
			console.log(data);
			data.unshift({bh:"",xm:"空数据"});
			initFhyCombobox("zz",data);
			initFhyCombobox("xc",data);
			initFhyCombobox("yd",data);
			initFhyCombobox("fxyd",data);
			isFhyInit = false;
		}
	});
}
//初始化防护员列表
function initFhyCombobox(id,data){
	$("#"+id).combobox({
		valueField:"bh",
		textField:"xm",
		editable:false,
		formatter:function(row){
			if(row.bh!=""){
				return row.xm+"("+row.age+"岁|政治面貌："+row.zzmm+")";
			}else{
				return row.xm;
			}
		},
		onShowPanel:onShowComboPanel
	});
	$("#"+id).combobox("loadData",data);
	if(!isFhyInit){
		if(data.length>0){
			$("#"+id).combobox('setValue', data[0].bh);
		}
	}
}
//初始化数据修改表单
function initEditFormData(){
	var bordercolor = "orange";
	try{
		var logintime = user.logintime;
		var hour = logintime.substring(11,13);
		if(parseInt(hour,10)>=20){
			bordercolor = "red";
		}
	}catch(e){
		
	}
	if(top.qgcjk_selectRow!=null){
		$("#myform *[field]").each(function(){
			var fieldtype = $(this).attr("field");
			var field = $(this).attr("id");
			var fieldvalue = top.qgcjk_selectRow[field];
			//console.log(fieldtype+"==="+field+"===="+fieldvalue);
			if(fieldtype=="textbox"){
				$("#"+field).textbox("setValue",fieldvalue);
			}else if(fieldtype=="numberbox"){
				$("#"+field).numberbox("setValue",fieldvalue);
			}else if(fieldtype=="timespinner"){
				$("#"+field).timespinner("setValue",fieldvalue);
			}else if(fieldtype=="combobox"){
				$("#"+field).combobox("setValue",fieldvalue);
			}else if(fieldtype=="datebox"){
				$("#"+field).datebox("setValue",fieldvalue);
			}
			if(fieldvalue==null || fieldvalue=="" || fieldvalue=="0"){
				$("#"+field).next().css("border","2px solid "+bordercolor);
			}
		});
		$("#id").val(top.qgcjk_selectRow.id);
		$("#qgcjkbh").val(top.qgcjk_selectRow.qgcjkbh);
	}
}

function resetData(){
	$("#myform").form("reset");
}

//保存数据
function saveData(){
	$("#myform").form('submit',{
		url:"service/api",
		onSubmit: function(){
			var isValid = $(this).form('validate');
			if(isValid){
				layer.msg("数据处理中，请稍候...",{icon:16,shade:[0.5,'#333333'],time:10000});
				$("#btnSave").linkbutton("disable");
				setTimeout(function(){
					$("#btnSave").linkbutton("enable");
				},5000);
			}
			return isValid;// 返回false终止表单提交
		},
		success: function(jsonstr){
			var json = eval('(' + jsonstr + ')');  
			if(json.code==0){
				var data = json.data;
				if(parseInt(data,10)==1){
					layer.alert("数据保存成功！",{icon:6},function(){
						parent.closeInputTab();
					});
				}else{
					layer.alert("数据保存失败！",{icon:5});
				}
			}else{
				layer.alert("数据保存失败！"+json.msg,{icon:5});
			}
		}
	});
}