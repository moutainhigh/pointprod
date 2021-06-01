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
    <title>每日一答</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointquestion" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>每日一答</h1>
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
                            <table id="question_list" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>类型</th>
                                    <th>标题</th>
                                    <th>正确答案</th>
                                    <th>显示时间</th>
                                    <th>平台</th>
                                    <th>版本</th>
                                    <th>用户分组</th>
                                    <th>更新时间</th>
                                    <th>编辑人</th>
                                    <th style="width: 10%;">操作</th>
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
                            <label for="lastname" class="col-sm-2 control-label">类型<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="question_type">
                                    <input id="questionType1" type="radio" name="question_type" value="1"/><label
                                            for="questionType1">单选</label>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input id="questionType2" type="radio" name="question_type" value="2"/><label
                                            for="questionType2">多选</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="txtMerchantName" class="col-sm-2 control-label">标题</label>
                            <div class="col-sm-10">
                                <textarea class="form-control" id="txtContent" rows="3" placeholder="标题"></textarea>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="txtMerchantName" class="col-sm-2 control-label">选项</label>
                            <div class="col-sm-10">
                                <textarea class="form-control" id="txtOption" rows="5"
                                          placeholder="选项（多个选项用'|'分隔）"></textarea>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="txtMerchantName" class="col-sm-2 control-label">正确选项</label>
                            <div class="col-sm-10">
                                <input class="form-control" id="txtRight" placeholder="正确选项（多个正确选项用'|'分隔）">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">产品版本<font color="red">*</font></label>
                            <div class="col-sm-8">
                                <div style="margin-top: 6px;" id="ver">
                                    <input id="ver1" type="checkbox" name="ver" value="888010000"/><label
                                            for="ver1">小智盈</label>
                                    <input id="ver2" type="checkbox" name="ver" value="888020000"/><label for="ver2">深度资金版</label>
                                    <input id="ver3" type="checkbox" name="ver" value="888080000"/><label
                                            for="ver3">掘金版</label>
                                    <input id="ver4" type="checkbox" name="ver" value="888010400"/><label for="ver4">小智盈过期</label>
                                    <input id="ver5" type="checkbox" name="ver" value="888020400"/><label for="ver5">大师过期</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">发布平台<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="platfrom">
                                    <input id="plat1" type="checkbox" name="ver" value="1"/><label
                                            for="plat1">PC</label>
                                    <input id="plat2" type="checkbox" name="ver" value="2"/><label
                                            for="plat2">APP</label>
                                    <input id="plat3" type="checkbox" name="ver" value="3"/><label
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
<script src="${request.contextPath}/static/js/pointquestion.index.1.js?v=111"></script>

<script>

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

        $("#ver1").attr("checked", false);
        $("#ver2").attr("checked", false);
        $("#ver3").attr("checked", false);
        $("#ver4").attr("checked", false);
        $("#ver5").attr("checked", false);
        $("#plat1").attr("checked", false);
        $("#plat2").attr("checked", false);
        $("#plat3").attr("checked", false);
        if (res.userGroup) {
            $("#GroupList").val(res.userGroup.split(",")).trigger('change');
        } else {
            $("#GroupList").val("").trigger('change');
        }
        if (res.productVersion) {
            var ver = res.productVersion.split(',');
            for (var i = 0; i < ver.length; i++) {
                if (ver[i] == "888010000") {
                    $("#ver1").attr("checked", true);
                }
                if (ver[i] == "888020000") {
                    $("#ver2").attr("checked", true);
                }
                if (ver[i] == "888080000") {
                    $("#ver3").attr("checked", true);
                }
                if (ver[i] == "888010400") {
                    $("#ver4").attr("checked", true);
                }
                if (ver[i] == "888020400") {
                    $("#ver5").attr("checked", true);
                }
            }
        }
        if (res.publishPlatFormType) {
            var plat = res.publishPlatFormType.split(',');
            for (var i = 0; i < plat.length; i++) {
                if (plat[i] == 1) {
                    $("#plat1").attr("checked", true);
                }
                if (plat[i] == 2) {
                    $("#plat2").attr("checked", true);
                }
                if (plat[i] == 3) {
                    $("#plat3").attr("checked", true);
                }
            }
        }
        $("#questionType1").attr("checked", false);
        $("#questionType2").attr("checked", false);
        if (res.questionType == 1) {
            $("#questionType1").attr("checked", true);
        } else {
            $("#questionType2").attr("checked", true);
        }
        $("#hiddenid").val(res.id);
        $("#txtContent").val(res.questionContent);
        $("#txtOption").val(res.questionOptions);
        $("#txtRight").val(res.questionRightoptions);
        $("#showTime").val(res.showTime);

        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    }

    function deletedata(id) {
        if (confirm("确认要删除吗？")) {
            var obj = new Object();
            obj.id = id;

            $.ajax({
                type: "POST",
                url: base_url + "/pointquestion/delete",
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

</script>
</body>
</html>