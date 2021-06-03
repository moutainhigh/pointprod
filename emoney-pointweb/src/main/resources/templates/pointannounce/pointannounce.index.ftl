<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/select2/select2.css">
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/jquery-multi-select/css/multi-select.css">
    <title>消息通知</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointannounce" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>消息通知</h1>
            <div class="breadcrumb" style="margin-top:-11px;">
                <button type="button" class="btn bg-blue" id="btnAdd">新增</button>
            </div>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="box box-primary" style="margin-top:15px;">
                <div class="box-header with-border">
                    <div class="form-group">
                        <div class="col-sm-2">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">类型：</label>
                            <select id="opType" class="form-control op"
                                    style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="2">商品上架</option>
                                <option value="4">最新活动</option>
                                <option value="5">通知</option>
                            </select>
                        </div>

                        <div class="col-sm-2">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">用户版本：</label>
                            <select id="opVer" class="form-control op"
                                    style="float:left;width:150px;margin-top:5px;">
                                <option value="">全部</option>
                                <option value="888010000">小智盈</option>
                                <option value="888020000">深度资金版</option>
                                <option value="888080000">掘金版</option>
                                <option value="888010400">小智盈过期</option>
                                <option value="888020400">大师过期</option>
                            </select>
                        </div>

                        <div class="col-sm-2">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">平台：</label>
                            <select id="opPlat" class="form-control op"
                                    style="float:left;width:150px;margin-top:5px;">
                                <option value="">全部</option>
                                <option value="1">PC</option>
                                <option value="2">APP</option>
                                <option value="3">微信</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="msg_list" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>类型</th>
                                    <th>正文</th>
                                    <th>发布时间</th>
                                    <th>创建时间</th>
                                    <th>编辑人</th>
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
                    <h4 class="modal-title">创建/编辑</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form">
                        <input type="hidden" id="hiddenid" value="">
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">类型<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <select class="form-control" name="msg_type" id="msg_type">
                                    <option value="2">商品上架</option>
                                    <option value="4">最新活动</option>
                                    <option value="5">通知</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">主题<font color="red">*</font></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="msg_content" id="msg_content"
                                       placeholder="主题">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">跳转地址<font color="red">*</font></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="msg_src" id="msg_src" placeholder="跳转地址">
                            </div>
                        </div>

                        <div class="form-group">
                            <!--实际为激活时间，需求强烈要求修改为登录-->
                            <label for="firstname" class="col-sm-2 control-label">发布时间</label>
                            <div class="col-sm-4">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <input type="text" class="form-control pull-right datepicker" id="publish_time">
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">产品版本<font color="red">*</font></label>
                            <div class="col-sm-8">
                                <div style="margin-top: 6px;" id="ver">
                                    <button type="button" style="margin-top: -6px;" id="checkVer"
                                            class="btn btn-primary btn-xs" value="全选">全选
                                    </button>
                                    <input id="ver1" type="checkbox" name="ver" class="ver" value="888010000"/><label
                                            for="ver1">小智盈</label>
                                    <input id="ver2" type="checkbox" name="ver" class="ver" value="888020000"/><label
                                            for="ver2">深度资金版</label>
                                    <input id="ver3" type="checkbox" name="ver" class="ver" value="888080000"/><label
                                            for="ver3">掘金版</label>
                                    <input id="ver4" type="checkbox" name="ver" class="ver" value="888010400"/><label
                                            for="ver4">小智盈过期</label>
                                    <input id="ver5" type="checkbox" name="ver" class="ver" value="888020400"/><label
                                            for="ver5">大师过期</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">发布平台<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="platfrom">
                                    <button type="button" style="margin-top: -6px;" id="checkPlat"
                                            class="btn btn-primary btn-xs" value="全选">全选
                                    </button>
                                    <input id="plat1" type="checkbox" name="plat" class="plat" value="1"/><label
                                            for="plat1">PC</label>
                                    <input id="plat2" type="checkbox" name="plat" class="plat" value="2"/><label
                                            for="plat2">APP</label>
                                    <input id="plat3" type="checkbox" name="plat" class="plat" value="3"/><label
                                            for="plat3">微信</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">用户分组</label>
                            <div class="col-sm-10">
                                <select id="GroupList" class="select2" multiple="multiple" title="请选择"
                                        style="width: 100%;">
                                    <#if userGroupVOList?exists && userGroupVOList?size gt 0 >
                                        <#list userGroupVOList as item>
                                            <option value="${item.id}">${item.userGroupName}</option>
                                        </#list>
                                    </#if>
                                </select>
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
<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.form.js"></script>
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.zh-CN.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2_locale_zh-CN.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/jquery-multi-select/js/jquery.multi-select.js"></script>

<script>
    //版本全选
    var verBtn = document.getElementById('checkVer');
    var verInput = document.getElementsByClassName('ver');
    verBtn.onclick = function () {
        if (verBtn.value === "全选") {
            verBtn.value = "取消全选";
            verBtn.innerHTML = "取消全选";
            for (var i = 0; i < verInput.length; i++) {
                verInput[i].checked = true;
            }
        } else {
            for (var i = 0; i < verInput.length; i++) {
                verInput[i].checked = false;
            }
            verBtn.value = "全选";
            verBtn.innerHTML = "全选";
        }
        for (var i = 0; i < verInput.length; i++) {
            verInput[i].onclick = function () {
                if (!this.checked) {
                    verBtn.value = "全选";
                    verBtn.innerHTML = "全选";
                }
            }
        }
    };
    $('.ver').on('click', function () {
        if ($("#ver input[type=checkbox]:checked").length === 5) {
            verBtn.value = "取消全选";
            verBtn.innerHTML = "取消全选";
        }
    })

    //平台全选
    var platBtn = document.getElementById('checkPlat');
    var platInput = document.getElementsByClassName('plat');
    platBtn.onclick = function () {
        if (platBtn.value === "全选") {
            platBtn.value = "取消全选";
            platBtn.innerHTML = "取消全选";
            for (var i = 0; i < platInput.length; i++) {
                platInput[i].checked = true;
            }
        } else {
            for (var i = 0; i < platInput.length; i++) {
                platInput[i].checked = false;
            }
            platBtn.value = "全选";
            platBtn.innerHTML = "全选";
        }
        for (var i = 0; i < platInput.length; i++) {
            platInput[i].onclick = function () {
                if (!this.checked) {
                    platBtn.value = "全选";
                    platBtn.innerHTML = "全选";
                }
            }
        }
    }
    $('.plat').on('click', function () {
        if ($("#platfrom input[type=checkbox]:checked").length === 3) {
            platBtn.value = "取消全选";
            platBtn.innerHTML = "取消全选";
        }
    })

    $('.select2').select2();

    $('.datepicker').datetimepicker({
        language: 'zh-CN',
        format: 'yyyy-mm-dd hh:ii:00',
        minDate: 0
    });

    $('#btnAdd').on('click', function () {
        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    });

    // search btn
    $('.op').on('change', function () {
        msgTable.ajax.reload();
    });

    $('.btnSave').on('click', function () {
        var obj = new Object();
        obj.id = $("#hiddenid").val();
        obj.msgType = $("#msg_type option:selected").val();
        obj.msgContent = $("#msg_content").val();
        obj.msgSrc = $("#msg_src").val();
        var ver = "";
        $("#ver input[type=checkbox]:checked").each(function () {
            ver += $(this).val() + ',';
        })
        obj.productVersion = ver;
        var plat = "";
        $("#platfrom input[type=checkbox]:checked").each(function () {
            plat += $(this).val() + ',';
        })
        obj.plat = plat;
        var str = "";
        var goodsArr = $("#GroupList").select2("val");
        for (var i = 0; i < goodsArr.length; i++) {
            str += goodsArr[i];
            if (i + 1 < goodsArr.length) {
                str += ",";
            }
        }
        obj.groupList = str;
        obj.publishTime = $("#publish_time").val();

        if (!validate(obj)) {
            return false;
        }
        $.ajax({
            type: "POST",
            url: base_url + "/pointannounce/edit",
            data: obj,
            datatype: "text",
            success: function (data) {
                $(".btnSave").attr("disabled", false);
                if (data == "success") {
                    msgTable.ajax.reload();
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

    function validate(obj) {
        if (!obj.msgType) {
            alert("请选择类型");
            return false;
        }
        if (!obj.msgContent) {
            alert("请填写主题");
            return false;
        }
        if (!obj.productVersion) {
            alert("请选择产品版本");
            return false;
        }
        if (!obj.publishTime) {
            alert("请填写发布时间");
            return false;
        }
        if (!obj.productVersion) {
            alert("请勾选用户版本");
            return false;
        }
        if (!obj.plat) {
            alert("请勾选发布平台");
            return false;
        }
        return true;
    }

    function editdata(id) {
        var jsondata = $('#json' + id).val();
        var res = JSON.parse(jsondata);

        if (res.userGroup) {
            $("#GroupList").val(res.userGroup.split(",")).trigger('change');
        } else {
            $("#GroupList").val("").trigger('change');
        }
        if (res.productVersion) {
            var ver = res.productVersion.split(',');
            if (ver.length >= 5) {
                verBtn.value = "取消全选";
                verBtn.innerHTML = "取消全选";
            }
            for (var i = 0; i < ver.length; i++) {
                if (ver[i] == "888010000") {
                    verInput[0].checked = true;
                }
                if (ver[i] == "888020000") {
                    verInput[1].checked = true;
                }
                if (ver[i] == "888080000") {
                    verInput[2].checked = true;
                }
                if (ver[i] == "888010400") {
                    verInput[3].checked = true;
                }
                if (ver[i] == "888020400") {
                    verInput[4].checked = true;
                }
            }
        }
        if (res.publishPlatFormType) {
            var plat = res.publishPlatFormType.split(',');
            if (plat.length >= 3) {
                platBtn.value = "取消全选";
                platBtn.innerHTML = "取消全选";
            }
            for (var i = 0; i < plat.length; i++) {
                if (plat[i] == 1) {
                    platInput[0].checked = true;
                }
                if (plat[i] == 2) {
                    platInput[1].checked = true;
                }
                if (plat[i] == 3) {
                    platInput[2].checked = true;
                }
            }
        }
        $("#hiddenid").val(res.id);
        $("#msg_type").val(res.msgType);
        $("#msg_content").val(res.msgContent);
        $("#msg_src").val(res.msgSrc);
        $("#publish_time").val(moment(res.publishTime).format("YYYY-MM-DD HH:mm:ss"));
        if (res.productVersion) {
            var ver = res.productVersion.split(',');
            for (var i = 0; i < ver.length; i++) {
                if (ver[i] == 1) {
                    $("#ver1").attr("checked", true);
                }
                if (ver[i] == 2) {
                    $("#ver2").attr("checked", true);
                }
                if (ver[i] == 3) {
                    $("#ver3").attr("checked", true);
                }
            }
        }

        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    }

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
        $("#msg_content").val("");
        $("#msg_src").val("");
        $("#publish_time").val("");
        for (var i = 0; i < verInput.length; i++) {
            verInput[i].checked = false;
        }
        for (var i = 0; i < platInput.length; i++) {
            platInput[i].checked = false;
        }
        verBtn.value = "全选";
        verBtn.innerHTML = "全选";
        platBtn.value = "全选";
        platBtn.innerHTML = "全选";

        $("#modal-default").modal('hide');
    }

    // init date tables
    var msgTable = $("#msg_list").DataTable({
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
            url: base_url + "/pointannounce/pageList",
            type: "post",
            data: function (d) {
                var obj = {};
                obj.msgType = $("#opType").val();
                obj.ver = $("#opVer").val();
                obj.plat = $("#opPlat").val();
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
                "data": 'msgType',
                "render": function (data, type, row) {
                    if (data) {
                        switch (data) {
                            case 2:
                                return "商品上架";
                                break;
                            case 4:
                                return "活动上新";
                                break;
                            case 5:
                                return "通知";
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
                "data": 'msgContent'
            },
            {
                "data": 'publishTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {
                "data": 'createTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {
                "data": 'updateBy'
            }
        ],
        fnDrawCallback: function () {
            let api = this.api();
            api.column(0).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        columnDefs: [{
            targets: 6,
            render: function (data, type, row, meta) {
                var html = "<button type=\"button\" class=\"btn btn-primary btn-flat btn-sm\" onclick='editdata(" + row.id + ")'>编辑</button>";
                html += "<input type='hidden' id='json" + row.id + "' value='" + JSON.stringify(row) + "'>";
                return html;
            }
        }]
    });
</script>

</body>
</html>
