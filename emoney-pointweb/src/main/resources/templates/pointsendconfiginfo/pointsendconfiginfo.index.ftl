<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.min.css">
    <title>购买赠送配置</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointsendconfiginfo" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>购买赠送配置</h1>
            <div class="breadcrumb" style="margin-top:-11px;">
                <button type="button" class="btn bg-blue" id="btnAdd">新增</button>
            </div>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="box box-primary" style="margin-top:15px;">
                <div class="box-header with-border">
                    <div class="form-group">
                        <div class="col-lg-2">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">购买类型：</label>
                            <select id="opType" class="form-control opType"
                                    style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="1">新增</option>
                                <option value="2">再购</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="sendconfiginfo_list" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>产品版本</th>
                                    <th>购买类型</th>
                                    <th>赠送比例</th>
                                    <th>赠送数量</th>
                                    <th>编辑人</th>
                                    <th>更新时间</th>
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

    <div class="modal fade" id="modal-default" role="dialog" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close btnClose" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">新增/编辑</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <input type="hidden" id="hiddenid" value="">
                        <div class="form-group">
                            <label class="col-sm-2 control-label">产品版本：</label>
                            <div class="col-sm-4">
                                <select id="ver" class="col-sm-4 form-control">
                                    <option value="888010000">小智盈</option>
                                    <option value="888020000">深度资金版</option>
                                    <option value="888080000">掘金版</option>
                                    <option value="888040000">五星价值</option>
                                    <option value="888090000">五星波段</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">购买类型<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="buyType">
                                    <input id="buyType1" type="radio" name="buyType" value="1" checked/><label
                                            for="buyType1">新增</label>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input id="buyType2" type="radio" name="buyType" value="2"/><label
                                            for="buyType2">再购</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">赠送比例<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" id="ratio" placeholder="赠送比例" maxlength="10">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">赠送数量<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" id="pointNum" placeholder="赠送数量" maxlength="10">
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default btnClose">关闭</button>
                    <button type="button" class="btn btn-primary btnSave">保存</button>
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
    // init date tables
    var sendconfigTable = $("#sendconfiginfo_list").DataTable({
        "processing": true,
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
            url: base_url + "/pointsendconfiginfo/pageList",
            type: "post",
            data: function (d) {
                var obj = {};
                obj.opType = $("#opType").val();
                return obj;
            }
        },
        "searching": true,
        "ordering": true,
        "scrollX": true,
        "columns": [
            {
                "data": null
            },
            {
                "data": 'productVersion',
                "render": function (data, type, row) {
                    if (data) {
                        switch (data) {
                            case "888010000":
                                return "小智盈";
                                break;
                            case "888020000":
                                return "深度资金版";
                                break;
                            case "888080000":
                                return "掘金版";
                                break;
                            case "888040000":
                                return "五星价值";
                                break;
                            case "888090000":
                                return "五星波段";
                                break;
                            default:
                                return "";
                                break;
                        }
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": 'buyType',
                "render": function (data, type, row) {
                    if (data) {
                        switch (data) {
                            case 1:
                                return "新增";
                                break;
                            case 2:
                                return "再购";
                                break;
                            default:
                                return "";
                                break;
                        }
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": 'ratio',
                "render": function (data, type, row) {
                    if (data) {
                        return data + "%";
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": 'pointNum'
            },
            {"data": 'updateBy'},
            {
                "data": 'updateTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            }
        ],
        fnDrawCallback: function () {
            let api = this.api();
            api.column(0).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        columnDefs: [{
            targets: 7,
            render: function (data, type, row, meta) {
                var html = "<button type=\"button\" class=\"btn btn-primary btn-flat btn-sm\" onclick='editdata(" + row.id + ")'>编辑</button>";
                html += "<button type=\"button\" class=\"btn btn-danger btn-flat btn-sm\" onclick='deletedata(" + row.id + ")'>删除</button>";
                html += "<input type='hidden' id='json" + row.id + "' value='" + JSON.stringify(row) + "'>";
                return html;
            }
        }]
    });

    // search btn
    $('.opType').on('change', function () {
        sendconfigTable.ajax.reload();
    });

    function editdata(id) {
        var jsondata = $('#json' + id).val();
        var res = JSON.parse(jsondata);

        $("#hiddenid").val(res.id);
        $("#ratio").val(res.ratio);
        $("#pointNum").val(res.pointNum);
        $("#ver").val(res.productVersion);
        if (res.buyType == 1) {
            $("#buyType1").attr("checked", true);
        } else {
            $("#buyType2").attr("checked", true);
        }

        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    }

    $('.btnSave').on('click', function () {
        var obj = new Object();
        obj.id = $("#hiddenid").val();
        obj.ver = $("#ver option:selected").val();
        obj.ratio = $("#ratio").val();
        obj.pointNum = $("#pointNum").val();
        obj.buyType = $("#buyType input[type=radio]:checked").val();

        if (!validate(obj)) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: base_url + "/pointsendconfiginfo/edit",
            data: obj,
            datatype: "text",
            success: function (data) {
                $(".btnSave").attr("disabled", false);
                if (data == "success") {
                    sendconfigTable.ajax.reload();
                    clertAndCloseModal();
                } else {
                    alert(data);
                }
            },
            beforeSend: function () {
                $(".btnSave").attr("disabled", true);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    });

    function deletedata(id) {
        if (confirm("确认要删除吗？")) {
            var obj = new Object();
            obj.id = id;

            $.ajax({
                type: "POST",
                url: base_url + "/pointsendconfiginfo/delete",
                data: obj,
                datatype: "text",
                success: function (data) {
                    if (data == "success") {
                        sendconfigTable.ajax.reload();
                    } else {
                        alert("删除失败");
                    }
                },
                beforeSend: function () {
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                }
            });
        }
    }

    function validate(obj) {
        if (!obj.ver) {
            alert("请选择购买产品版本");
            return false;
        }
        if (!obj.ratio) {
            alert("请填写赠送比例");
            return false;
        }
        if (!obj.buyType) {
            alert("请选择购买类型");
            return false;
        }
        return true;
    }

    $('#btnAdd').on('click', function () {
        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    });

    $('.btnClose').on('click', function () {
        if ($("#Form input").val() != "") {
            if (confirm("当前有数据未保存，确认要关闭窗口吗?")) {
                clertAndCloseModal();
            }
        } else {
            clertAndCloseModal();
        }
    });

    function clertAndCloseModal() {
        $("#hiddenid").val();
        $("#ratio").val("");
        $("#buyType1").attr("checked", false);
        $("#buyType2").attr("checked", false);

        $("#modal-default").modal('hide');
    }
</script>