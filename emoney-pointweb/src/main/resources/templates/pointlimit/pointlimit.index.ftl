<!DOCTYPE html>
<html>
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <title>限额配置</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointlimit" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>限额配置</h1>
            <#--<div class="breadcrumb" style="margin-top:-11px;">
                <button type="button" class="btn bg-blue" id="btnAdd">新增</button>
            </div>-->
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body" >
                            <table id="limit_list" class="table table-bordered table-striped" width="100%" >
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>类型</th>
                                    <th>限额对象</th>
                                    <th>限额值</th>
                                    <th>时间</th>
                                    <th>编辑人</th>
                                    <#--<th>操作</th>-->
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

    <div class="modal fade" id="modal-default" tabindex="-1" role="dialog"  aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close btnClose" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title" >创建/编辑</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" >
                        <input type="hidden" id="hiddenid" value="">
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">限额类型<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <select class="form-control" id="task_type">
                                    <option value="1">发放</option>
                                    <option value="2">兑换</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">限额对象<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <select class="form-control" id="task_type">
                                    <option value="1">系统</option>
                                    <option value="2">用户</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">限额值<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" id="task_points" placeholder="限额值" maxlength="50" >
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">提示语<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" id="task_points" placeholder="提示语" maxlength="50" >
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer alipay wechatpay">
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
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/bower_components/moment/moment.min.js"></script>
<script src="${request.contextPath}/static/js/pointlimit.index.1.js"></script>

</body>
</html>