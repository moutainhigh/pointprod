<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <link rel="stylesheet" href="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.css">
    <title>兑换明细</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointorder" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>兑换明细</h1>
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
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">商品类型：</label>
                            <select id="opType" class="form-control" style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="1">产品使用期</option>
                                <option value="2">优惠券</option>
                                <option value="3">新功能体验</option>
                                <option value="4">门票兑换</option>
                                <option value="5">实物兑换</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body" >
                            <table id="order_list" class="table table-bordered table-striped" width="100%" >
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>商品名称</th>
                                    <th>商品类型</th>
                                    <th>用户账号</th>
                                    <th>订单号</th>
                                    <th>兑换时间</th>
                                    <th>手机号掩码</th>
                                    <th>加密手机号</th>
                                    <th>用户地址</th>
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

<@netCommon.commonScript />
<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.form.js"></script>
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.js"></script>
<script src="${request.contextPath}/static/js/pointorder.index.1.js?v=111"></script>
<script>

    $("#exportData").on("click",function (){
        var url= base_url + "/pointorder/exportData?productType=" + $("#opType").val();
        window.open(url);
    });

</script>
</body>
</html>