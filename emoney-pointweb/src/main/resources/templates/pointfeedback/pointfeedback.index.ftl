<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.css">
    <style>
        .FilePicker div:nth-child(2) {
            width: 100% !important;
            height: 100% !important;
        }

        .disable {
            pointer-events: none;
        }
    </style>
    <title>商品配置</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointfeedback" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>意见反馈</h1>
            <div class="breadcrumb" style="margin-top:-11px;">
                <button type="button" class="btn bg-blue" id="exportData">导出</button>
            </div>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="box box-primary" style="margin-top:15px;">
                <div class="box-header with-border">
                    <div class="form-group">
                        <div class="col-lg-2">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">反馈类型：</label>
                            <select id="opType" class="form-control opType" style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="1">产品建议</option>
                                <option value="2">使用心得</option>
                                <option value="3">提问咨询</option>
                                <option value="4">其他建议</option>
                            </select>
                        </div>
                        <div class="col-lg-2">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">处理状态：</label>
                            <select id="opReply" class="form-control opType" style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="1">已处理</option>
                                <option value="2">待处理</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body" >
                            <table id="feedback_list" class="table table-bordered table-striped" width="100%" >
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>反馈类型</th>
                                    <th>提交时间</th>
                                    <th>版本</th>
                                    <th>账号</th>
                                    <th>加密手机号</th>
                                    <th>邮箱</th>
                                    <th>反馈内容</th>
                                    <th>图片</th>
                                    <th>最新进展</th>
                                    <th>回复意见</th>
                                    <th>是否采纳</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                                <tfoot></tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <div class="modal fade" id="modal-default" role="dialog"  aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close btnClose" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title" >回复</h4>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="hiddenid" value="">
                    <div class="form-group">
                        <label for="txtMerchantName">邮箱</label>
                        <input class="form-control" id="txtEmail" placeholder="标题" disabled>
                    </div>

                    <div class="form-group">
                        <label for="txtMerchantName">反馈内容</label>
                        <textarea class="form-control" id="txtContent" rows="15" placeholder="反馈内容" disabled></textarea>
                    </div>

                    <div class="form-group">
                        <label for="txtMerchantName">回复意见</label>
                        <textarea class="form-control" id="txtRemark" rows="15" placeholder="回复意见"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default btnClose">关闭</button>
                    <button type="button" class="btn btn-primary btnSave">确认</button>
                </div>
            </div>
        </div>
    </div>

    <!-- footer -->
    <@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!--富文本编辑框-->
<script src="${request.contextPath}/static/js/wangEditor.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.form.js"></script>
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>

<script>

    $("#exportData").on("click",function (){
        var url= base_url + "/pointfeedback/exportData?classType=" + $("#opType").val() + "&isReply=" + $("#opReply").val();
        window.open(url);
    });

    // init date tables
    var feedbackTable = $("#feedback_list").DataTable({
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
            url: base_url + "/pointfeedback/pageList",
            type:"post",
            data : function ( d ) {
                var obj = {};
                obj.classType=$("#opType").val();
                obj.isReply=$("#opReply").val();
                return obj;
            }
        },
        "searching": true,
        "ordering": true,
        "columns": [
            {
                "data": null
            },
            {
                "data": 'feedType',
                "render": function (data, type, row) {
                    if(data)
                    {
                        switch (data)
                        {
                            case 1:
                                return "产品建议";
                                break;
                            case 2:
                                return "使用心得";
                                break;
                            case 3:
                                return "提问咨询";
                                break;
                            case 4:
                                return "其他建议";
                                break;
                            default:
                                return "";
                                break;
                        }
                    }
                    else {
                        return "";
                    }
                }
            },
            {
                "data": 'createTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {"data":'pid'},
            {"data":'account'},
            {"data":'mobileX'},
            {"data":'email'},
            {
                "data": "suggest", "render": function (data, type, full, meta) {
                    if (data) {
                        return "<td><span title='" + data + "'>" + data.substring(0, 30) + "</span></td>";
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": "imgUrl", "render": function (data, type, full, meta) {
                    if (data) {
                        return "<a href='" + data + "' target='_blank'>点击查看图片</a>";
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": "remark", "render": function (data, type, full, meta) {
                    if (data) {
                        return "已处理";
                    } else {
                        return "未处理";
                    }
                }
            },
            {
                "data": "remark", "render": function (data, type, full, meta) {
                    if (data) {
                        return "<td><span title='" + data + "'>" + data.substring(0, 30) + "</span></td>";
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": "status", "render": function (data, type, full, meta) {
                    if (data) {
                        if(data!=0){
                            return "是"
                        }else {
                            return "否";
                        }
                    } else {
                        return "否";
                    }
                }
            }
        ],
        fnDrawCallback: function () {
            let api = this.api();
            api.column(0).nodes().each(function(cell, i) {
                cell.innerHTML =  i + 1;
            });
        },
        columnDefs: [{
            targets: 12,
            render: function (data, type, row, meta) {
                var html = "<button type=\"button\" class=\"btn btn-primary btn-flat btn-sm\" onclick='Reply(" + row.id + ")'>回复</button>";
                if(row.status!=1){
                    html += "<button type=\"button\" class=\"btn btn-success btn-flat btn-sm\" onclick='Adopt(" + row.id + ")'>采纳</button>";
                }
                html += "<input type='hidden' id='json" + row.id + "' value='" + JSON.stringify(row) + "'>";
                return html;
            }
        }]
    });

    // search btn
    $('.opType').on('change', function(){
        feedbackTable.ajax.reload();
    });

    $('.btnSave').on('click',function (){
        var obj=new Object();
        obj.id=$("#hiddenid").val();
        obj.remark=$("#txtRemark").val();

        $.ajax({
            type: "POST",
            url: base_url + "/pointfeedback/editReply",
            data: obj,
            datatype: "text",
            success: function (data) {
                if (data == "success") {
                    feedbackTable.ajax.reload();
                    clertAndCloseModal();
                }
                else {
                    alert(data);
                }
            },
            beforeSend: function () {
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    });

    function Reply(id){
        var jsondata = $('#json' + id).val();
        var res = JSON.parse(jsondata);

        $("#hiddenid").val(res.id);
        $("#txtEmail").val(res.email);
        $("#txtContent").val(res.suggest);
        $("#txtRemark").val(res.remark);

        $("#modal-default").modal({ backdrop: false, keyboard: false }).modal('show');
    }

    function Adopt(id){
        var obj=new Object();
        obj.id=id;

        $.ajax({
            type: "POST",
            url: base_url + "/pointfeedback/adopt",
            data: obj,
            datatype: "text",
            success: function (data) {
                if (data == "success") {
                    feedbackTable.ajax.reload();
                }
                else {
                    alert(data);
                }
            },
            beforeSend: function () {
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
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

    function clertAndCloseModal(){
        $("#hiddenid").val("");
        $("#txtEmail").val("");
        $("#txtContent").val("");
        $("#txtremark").val("");

        $("#modal-default").modal('hide');
    }
</script>