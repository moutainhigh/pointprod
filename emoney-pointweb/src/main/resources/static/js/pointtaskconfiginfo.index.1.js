$(function() {

	// init date tables
	var taskTable = $("#task_list").DataTable({
		"processing" : true,
		language: {
			"sProcessing": "处理中...",
			"sLengthMenu": "显示 _MENU_ 项结果",
			"sZeroRecords": "没有匹配结果",
			"sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
			"sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
			"sInfoFiltered": "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix": "",
			"sSearch": "搜索:",
			"sUrl": "",
			"sEmptyTable": "表中数据为空",
			"sLoadingRecords": "载入中...",
			"sInfoThousands": ",",
			"oPaginate": {
				"sFirst": "首页",
				"sPrevious": "上页",
				"sNext": "下页",
				"sLast": "末页"
			},
			"oAria": {
				"sSortAscending": ": 以升序排列此列",
				"sSortDescending": ": 以降序排列此列"
			}
		},
		"ajax": {
			url: base_url + "/pointtaskconfiginfo/pageList",
			type:"post",
			data : function ( d ) {
				var obj = {};
				obj.task_type=$("#opType").val();
				obj.task_status=$("#opStatus").val();
				return obj;
			}
		},
		"searching": true,
		"ordering": true,
		"scrollX": true,	// scroll x，close self-adaption
		"columns": [
			{
				"data": null
			},
			{
				"data": 'taskId',
				"render": function (data, type, row) {
					return String(data);
				}
			},
			{
				"data": 'taskType',
				"render": function (data, type, row) {
					if(data)
                    {
                        //新手任务、成长任务、贡献任务、活动任务、其他奖励任务
                        switch (data)
                        {
                            case 1:
                                return "新手任务";
                                break;
                            case 2:
                                return "成长任务";
                                break;
                            case 3:
                                return "贡献任务";
                                break;
                            case 4:
                                return "活动任务";
                                break;
                            default:
                                return "其他";
                                break;
                        }
                    }
					else {
					    return "";
                    }
				}
			},
			{"data":'taskOrder'},
			{"data": 'taskName'},
			{"data": 'taskPoints'},
			{
				"data": 'isDailyTask',
                "render": function (data, type, row) {
                    return data == 1 ?"是" : "否";
                }
			},
			{
				"data": 'isDirectional',
				"render": function (data, type, row) {
					return data == 1 ?"是" : "否";
				}
			},
			{
				"data": 'dailyJoinTimes'
			},
			{
				"data": 'taskStartTime',
				"render": function (data, type, row) {
					return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
				}
			},
            {
                "data": 'taskEndTime',
                "render": function (data, type, row) {
					return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
			{
			    "data": 'publishPlatFormType',
				"render": function (data, type, row) {
					if(data){
						data=data.substring(0,data.length-1).replace("1","PC");
						data=data.replace("2","APP");
						data=data.replace("3","微信");
						return data;
					}else {
						return "";
					}
				}
			},
			{
				"data": 'productVersion',
				"render": function (data, type, row) {
					if(data){
						data=data.substring(0,data.length-1).replace("888010000","小智盈");
						data=data.replace("888020000","深度资金版");
						data=data.replace("888080000","掘金版");
						data=data.replace("888010400","小智盈过期");
						data=data.replace("888020400","大师过期");
						return data;
					}else {
						return "";
					}
				}
			},
			{
				"data":'updateBy'
			}
		],
		fnDrawCallback: function () {
			//自增长替换第一列
			this.api().column(0).nodes().each(function (cell, i) {
				cell.innerHTML = i + 1;
			});
		},
		columnDefs: [{
		targets: 14,
		render: function (data, type, row, meta) {
			var html = "<button type=\"button\" class=\"btn btn-primary btn-flat btn-sm\" onclick='editdata(" + row.id + ")'>编辑</button>";
			if(row.subId){
				html += "<button type=\"button\" class=\"btn btn-info btn-flat btn-sm\" onclick='copytask(" + row.id + ")'>复制</button>";
			}
			html += "<input type='hidden' id='json" + row.id + "' value='" + JSON.stringify(row) + "'>";
			return html;
		}
	}]
	});

	// search btn
	$('.op').on('change', function(){
		taskTable.ajax.reload();
	});

	//显示活动图片配置
	$('#task_type').on('change', function(){
		var optype=$("#task_type option:selected").val();
		if(optype=="4"){
			$("#imgtype").show();
			$("#isshow").show();
			$("#ImgList").show();
		}else {
			$("#imgtype").hide();
			$("#isshow").hide();
			$("#ImgList").hide();
		}
	});

	$('#btnAdd').on('click', function(){
		$("#ver1").attr("checked", false);
		$("#ver2").attr("checked", false);
		$("#ver3").attr("checked", false);
		$("#ver4").attr("checked", false);
		$("#ver5").attr("checked", false);
		$("#plat1").attr("checked", false);
		$("#plat2").attr("checked", false);
		$("#plat3").attr("checked", false);
		$("#show1").attr("checked", false);
		$("#show2").attr("checked", false);
		$("#directional1").attr("checked", false);
		$("#directional2").attr("checked", false);
		$("#daily1").attr("checked", false);
		$("#daily2").attr("checked", false);
		$("#big").attr("checked", false);
		$("#small").attr("checked", false);

		$('#tasktime').val("");
		$('#activation').val("");
		$('#expire').val("");
		$("#task_type").attr("disabled",false);
		$("#task_points").attr("disabled",false);
		$("#tasktime").attr("disabled",false);
		$("#activation").attr("disabled",false);
		$("#expire").attr("disabled",false);
		$("#buttontext").attr("disabled",false);
		$("#taskorder").attr("disabled",false);
		$("#pcurl").attr("disabled",false);
		$("#appurl").attr("disabled",false);
		$("#wechaturl").attr("disabled",false);
		$("#jointimes").attr("disabled",false);
		$(".form input[type='radio']").attr("disabled",false);
		$(".form input[type='checkbox']").attr("disabled",false);
		$(".FilePicker").attr("disabled",false);
		$(".cleartime").removeClass("disable");

		$.ajax({
			type: "POST",
			url: base_url + "/pointtaskconfiginfo/getTaskId",
			datatype: "text",
			success: function (data) {
				$("#hiddentaskId").val(data);
				$("#taskId").html(data);
			},
			beforeSend: function () {
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
			}
		});

		$("#modal-default").modal({ backdrop: false, keyboard: false }).modal('show');
	});

	$('.btnSave').on('click', function(){
		var obj=new Object();
		obj.id=$("#hiddenid").val();
		obj.taskId=$("#hiddentaskId").val();
		obj.subid=$("#subId").val();
		obj.tasktype=$("#task_type option:selected").val();
		obj.taskname=$("#task_name").val();
		obj.taskpoints=$("#task_points").val();
		obj.starttime=$("#starttime").val();
		obj.endtime=$("#endtime").val();
		obj.taskremark=editor.txt.html();
		obj.daily=$("#daily input[type=radio]:checked").val();
		obj.is_directional=$("#Directional input[type=radio]:checked").val();
		obj.is_bigimg=$("#isBigImg input[type=radio]:checked").val();
		obj.sendType=$("#sendType input[type=radio]:checked").val();
		obj.jointimes=$("#jointimes").val();
		obj.taskorder=$("#taskorder").val();

		var str = "";
		var goodsArr = $("#GroupList").select2("val");
		for (var i = 0; i < goodsArr.length; i++) {
			str += goodsArr[i];
			if (i + 1 < goodsArr.length) {
				str += ",";
			}
		}
		obj.groupList = str;
		var ver="";
		$("#ver input[type=checkbox]:checked").each(function() {
			ver +=  $(this).val() + ',';
		})
		obj.ver=ver;
		obj.ishomepage=$("#isHomePage input[type=radio]:checked").val();
		var plat="";
		$("#platfrom input[type=checkbox]:checked").each(function() {
			plat +=  $(this).val() + ',';
		})
		obj.platfrom=plat;
		obj.pcurl=$("#pcurl").val();
		obj.appurl=$("#appurl").val();
		obj.wechaturl=$("#wechaturl").val();
		obj.buttontext=$("#buttontext").val();
		obj.pcimageurl = $("#pcimg").val();
		obj.appimageurl = $("#appimg").val();
		obj.wechatimageurl = $("#wechatimg").val();

		if(!validate(obj)){
			return false;
		}
		$.ajax({
			type: "POST",
			url: base_url + "/pointtaskconfiginfo/edit",
			data: obj,
			datatype: "text",
			success: function (data) {
				$(".btnSave").attr("disabled",false);
				if (data == "success") {
					taskTable.ajax.reload();
					clertAndCloseModal();
				}
				else {
					alert(data);
				}
			},
			beforeSend: function () {
				$(".btnSave").attr("disabled",true);
			},
			error: function (XMLHttpRequest, textStatus, errorThrown) {
			}
		});
	});

	function validate(obj){
		if(!obj.is_directional){
			alert("请选择是否为定向任务");
			return false;
		}
		if(!obj.tasktype){
			alert("请选择任务类型");
			return false;
		}
		if(!obj.sendType){
			alert("请选择是否立即发送");
			return false;
		}
		if(!obj.taskname){
			alert("请填写任务名称");
			return false;
		}
		if(!obj.taskpoints){
			alert("请填写任务积分");
			return false;
		}
		if(!obj.taskorder){
			alert("顺序不能为空");
			return false;
		}
		if(obj.starttime==obj.endtime){
			alert("时间区间不能为空");
			return false;
		}
		if(!obj.daily){
			alert("请选择是否为每日任务");
			return false;
		}
		if(!obj.jointimes){
			alert("请填写任务次数");
			return false;
		}
		if(!obj.ver){
			alert("请选择版本");
			return false;
		}
		if(!obj.platfrom){
			alert("请选择发布平台");
			return false;
		}
		var plat=obj.platfrom.split(',');
		for (var i=0;i<plat.length;i++)
		{
			if(plat[i]==1&&!obj.pcurl)
			{
				alert("请填写PC跳转链接");
				return false;
			}
			if(plat[i]==2&&!obj.appurl)
			{
				alert("请填写APP跳转链接");
				return false;
			}
			if(plat[i]==3&&!obj.wechaturl)
			{
				alert("请填写微信跳转链接");
				return false;
			}
		}
		return true;
	}

	$('.btnClose').on('click', function(){
		if ($("#Form input").val() != "") {
			if (confirm("当前有数据未保存，确认要关闭窗口吗?")) {
				clertAndCloseModal();
			}
		}
		else {
			clertAndCloseModal();
		}
	});
});
