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
    <link rel="stylesheet" href="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.css">
    <title>积分调整</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointsendrecord" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>积分调整明细</h1>
            <div class="breadcrumb" style="margin-top:-11px;">
                <button type="button" class="btn bg-blue" id="exportData">导出</button>
            </div>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="box box-primary" style="margin-top:15px;">
                <div class="box-header with-border">
                    <div class="form-group">
                        <div class="col-lg-11">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">状态类型：</label>
                            <select id="opType" class="form-control" style="float:left;width:150px;margin-top:5px;">
                                <option value="-1">全部</option>
                                <option value="1">成功</option>
                                <option value="0">失败</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="sendrecorddetail_list" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>类型</th>
                                    <th>任务名称</th>
                                    <th>调整说明</th>
                                    <th>用户名</th>
                                    <th>调整</th>
                                    <th>时间</th>
                                    <th>状态</th>
                                    <th>状态原因</th>
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

    <!-- footer -->
    <@netCommon.commonFooter />
</div>

<input id="hiddenbatchId" type="hidden" value="${batchId}">

<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.form.js"></script>
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.js"></script>
<script src="${request.contextPath}/static/js/pointsendrecord.detail.1.js"></script>
<script src="${request.contextPath}/static/js/webuploader.js"></script>
<script>

    $("#exportData").on("click",function (){
        var batchid=$("#hiddenbatchId").val();
        var classType=$("#opType").val();
        var url= base_url + "/pointsendrecord/exportData?classType=" + classType + "&batchId=" + batchid;
        window.open(url);
    });

</script>
</body>
</html>
