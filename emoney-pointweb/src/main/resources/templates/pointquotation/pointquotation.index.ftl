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
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/select2/select2.css">
    <link rel="stylesheet"
          href="${request.contextPath}/static/adminlte/bower_components/jquery-multi-select/css/multi-select.css">
    <title>语录编辑</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointquotation" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>语录编辑</h1>
            <div class="breadcrumb" style="margin-top:-11px;">
                <button type="button" class="btn bg-blue" id="btnAdd">新增</button>
            </div>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="quotation_list" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>内容</th>
                                    <th>显示时间</th>
                                    <th>平台</th>
                                    <th>版本</th>
                                    <th>用户分组</th>
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
                            <label for="firstname" class="col-sm-2 control-label">语录内容<font color="red">*</font></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="content" id="content" placeholder="语录内容">
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
                                    <input id="ver2" type="checkbox" name="ver"  class="ver" value="888020000"/><label for="ver2">深度资金版</label>
                                    <input id="ver3" type="checkbox" name="ver"  class="ver" value="888080000"/><label
                                            for="ver3">掘金版</label>
                                    <input id="ver4" type="checkbox" name="ver"  class="ver" value="888010400"/><label for="ver4">小智盈过期</label>
                                    <input id="ver5" type="checkbox" name="ver"  class="ver" value="888020400"/><label for="ver5">大师过期</label>
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
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">显示时间</label>
                            <div class="col-sm-4">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <input type="text" class="form-control pull-right datepicker" id="showTime">
                                </div>
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
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.zh-CN.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2_locale_zh-CN.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/jquery-multi-select/js/jquery.multi-select.js"></script>
<script src="${request.contextPath}/static/js/pointquotation.index.1.js?v=20210602"></script>

<script th:type="javascript">

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
    $('.ver').on('click',function (){
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
    $('.plat').on('click',function (){
        if ($("#platfrom input[type=checkbox]:checked").length === 3) {
            platBtn.value = "取消全选";
            platBtn.innerHTML = "取消全选";
        }
    })

    $('.select2').select2();

    $('.datepicker').datetimepicker({
        language: 'zh-CN',
        minView: "month",
        format: 'yyyy-mm-dd',
        minDate: 0
    });

    function editdata(id) {
        var jsondata = $('#json' + id).val();
        var res = JSON.parse(jsondata);

        $("#hiddenid").val(res.id);
        $("#content").val(res.content);
        $("#showTime").val(res.showTime);
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

        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    }

    function deletedata(id) {
        if (confirm("确认要删除吗？")) {
            var obj = new Object();
            obj.id = id;

            $.ajax({
                type: "POST",
                url: base_url + "/pointquotation/delete",
                data: obj,
                datatype: "text",
                success: function (data) {
                    if (data == "success") {
                        location.reload();
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

    function clertAndCloseModal() {
        $("#hiddenid").val("");
        $("#content").val("");
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
        $("#GroupList").val("").trigger('change');
        $("#showTime").val("");

        $("#modal-default").modal('hide');
    }
</script>
</body>
</html>