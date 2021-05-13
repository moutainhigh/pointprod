<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
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
            <h1>积分调整</h1>
            <div class="breadcrumb" style="margin-top:-11px;">
                <button type="button" class="btn bg-blue" id="btnAdd">新增</button>
            </div>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="box box-primary" style="margin-top:15px;">
                <div class="box-header with-border">
                    <div class="form-group">
                        <div class="col-lg-11">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">类型：</label>
                            <select id="opType" class="form-control" style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="1">发放</option>
                                <option value="2">扣除</option>
                            </select>
                            <#--<label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:36px;">任务名称:</label>
                            <input id="taskname" class="form-control" style="float:left;width:300px;margin-top:5px;">-->
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body" >
                            <table id="sendrecord_list" class="table table-bordered table-striped" width="100%" >
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>类型</th>
                                    <th>任务名称</th>
                                    <th>成功数/总数</th>
                                    <th>备注</th>
                                    <th>时间</th>
                                    <th>编辑人</th>
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
                    <h4 class="modal-title" >创建/编辑</h4>
                </div>
                <div class="modal-body">
                    <form class="form-horizontal form" role="form" ACTION="/pointsendrecord/importUserData" method="post" enctype="multipart/form-data">
                        <input type="hidden" id="hiddenid" value="">
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">选择任务<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <select class="form-control" name="taskId" id="task_type">
                                    <#if tasklist?exists && tasklist?size gt 0 >
                                        <#list tasklist as task>
                                            <option value="${task.taskId}">${task.taskName}</option>
                                        </#list>
                                    </#if>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">处理方式<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="handleType">
                                    <input id="ver1" type="radio" name="ver" value="1" checked onclick="changeType();"/><label for="ver1" onclick="changeType();">批量导入</label>
                                    <input id="ver2" type="radio" name="ver" value="2" onclick="changeType();"/><label for="ver2" onclick="changeType();">单个导入</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group" style="display: none;" id="excelTab">
                            <label for="firstname" class="col-sm-2 control-label"></label>
                            <div class="col-sm-4">
                                    <input type="file" name="file" id="FilePicker" name="upfile" />
                            </div>
                        </div>

                        <div class="form-group" style="display: none;" id="inputTab">
                            <label for="firstname" class="col-sm-2 control-label"></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="EMtext" id="EMtext" placeholder="EM号/手机号" >
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">调整说明<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="remark" id="remark" placeholder="调整说明" maxlength="50" >
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default btnClose">关闭</button>
                    <button type="button" class="btn btn-primary btnSave">导入</button>
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
<script src="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.js"></script>
<script src="${request.contextPath}/static/js/pointsendrecord.index.1.js?v=111"></script>
<script src="${request.contextPath}/static/js/webuploader.js"></script>

</body>
</html>
