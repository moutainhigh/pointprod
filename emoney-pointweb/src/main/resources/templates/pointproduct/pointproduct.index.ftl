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
    <link rel="stylesheet" href="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.css">
     <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/summernote/summernote.min.css">
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
    <@netCommon.commonLeft "pointproduct" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>商品配置</h1>
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
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">商品类型：</label>
                            <select id="opType" class="form-control op" style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="1">产品使用期</option>
                                <option value="2">优惠券</option>
                                <option value="3">新功能体验</option>
                                <option value="4">特色服务</option>
                                <option value="5">实物兑换</option>
                            </select>
                        </div>

                        <div class="col-sm-2">
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">状态：</label>
                            <select id="opStatus" class="form-control op"
                                    style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="1">进行中</option>
                                <option value="2">未开始</option>
                                <option value="3">已结束</option>
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
                            <table id="product_list" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th>Id</th>
                                    <th>商品类型</th>
                                    <th>商品名称</th>
                                    <th>用户版本</th>
                                    <th>兑换类型</th>
                                    <th>所需积分</th>
                                    <th>所需现金</th>
                                    <th>总限额</th>
                                    <th>每人兑换限额</th>
                                    <th>开始时间</th>
                                    <th>结束时间</th>
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
                    <form class="form-horizontal form" role="form" method="post">
                        <input type="hidden" id="hiddenid" value="">
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">商品类型<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <select class="form-control" name="productType" id="productType">
                                    <#--产品使用期、优惠券、门票兑换券、新功能体验、实物兑换-->
                                    <option value="1">产品使用期</option>
                                    <option value="2">优惠券</option>
                                    <option value="3">新功能体验</option>
                                    <option value="4">特色服务</option>
                                    <option value="5">实物兑换</option>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="lastname" class="col-sm-2 control-label">兑换类型<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="exchange_type">
                                    <input id="exchange1" type="radio" name="exchange_type" value="0"/><label
                                            for="exchange1">积分</label>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input id="exchange2" type="radio" name="exchange_type" value="1"/><label
                                            for="exchange2">积分+现金</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">物流包|特权ID</label>
                            <div class="col-sm-6">
                                <div class="input-group">
                                    <input type="text" class="form-control" name="actCode" id="actCode"
                                           placeholder="物流包|特权ID|优惠券">
                                    <span class="input-group-btn">
                                        <button type="button" class="btn btn-info btn-flat" id="checkAcCode">检查</button>
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">pid</label>
                            <div class="col-sm-8">
                                <input type="text" class="form-control" name="actPid" id="actPid"
                                       placeholder="物流包Pid" disabled>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">商品有效期</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="actStartTime" id="actStartTime"
                                       placeholder="物流包开始时间" disabled>
                            </div>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="actEndTime" id="actEndTime"
                                       placeholder="物流包结束时间" disabled>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">商品名称<font color="red">*</font></label>
                            <div class="col-sm-8">
                                <input type="text" class="form-control" name="productName" id="productName"
                                       placeholder="商品名称">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">物流包原价</label>
                            <div class="col-sm-2">
                                <input type="text" class="form-control" name="actPrice" id="actPrice"
                                       placeholder="物流包原价" disabled>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">商品原价</label>
                            <div class="col-sm-2">
                                <input type="text" class="form-control" name="productPrice" id="productPrice"
                                       placeholder="商品原价">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">兑换时间</label>
                            <div class="col-sm-4">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <input type="text" class="form-control pull-right datepicker"
                                           id="exChangeStartTime">
                                </div>
                            </div>
                            <div class="col-sm-4">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <input type="text" class="form-control pull-right datepicker" id="exChangeEndTime">
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
                            <label for="firstname" class="col-sm-2 control-label">现金</label>
                            <div class="col-sm-2">
                                <input type="text" class="form-control" name="productCash" id="productCash"
                                       placeholder="现金">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">积分</label>
                            <div class="col-sm-2">
                                <input type="text" class="form-control" name="productPoint" id="productPoint"
                                       placeholder="积分">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">总限兑</label>
                            <div class="col-sm-2">
                                <input type="text" class="form-control" name="totalLimit" id="totalLimit"
                                       placeholder="总限兑">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">每人限兑</label>
                            <div class="col-sm-2">
                                <input type="text" class="form-control" name="perLimit" id="perLimit"
                                       placeholder="每人限兑">
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
                            <label for="txtMerchantName" class="col-sm-2 control-label">商品介绍</label>
                            <div class="col-sm-10">
                                <div id="txtContent"></div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="txtMerchantName" class="col-sm-2 control-label">使用说明</label>
                            <div class="col-sm-10">
                                <div id="txtRemark"></div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">统计分类</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="statisticalClassification"
                                       id="statisticalClassification"
                                       placeholder="统计分类">
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">附件</label>
                            <div class="col-sm-10">
                                <div id="pcPicUploader" style="width: 100%;">
                                    <div id="FileList" style="position:relative;width: 100%;" class="uploader-list">
                                    </div>
                                    <div id="FilePicker" class="FilePicker col-sm-2">上传附件</div>
                                    <span style="float: left;margin-top: 10px;">投资内参专用(<span style="color: red;">限1M内pdf文件</span>)</span>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">封面图</label>
                            <div class="col-sm-3">
                                <div id="pcPicUploader">
                                    <div id="pcPicFileList" style="position:relative;" class="uploader-list">
                                    </div>
                                    <div id="pcPicFilePicker" class="FilePicker">上传PC图片</div>
                                </div>
                            </div>
                            <div class="col-sm-3">
                                <div id="appPicUploader">
                                    <div id="appPicFileList" style="position:relative;" class="uploader-list">
                                    </div>
                                    <div id="appPicFilePicker" class="FilePicker">上传APP图片</div>
                                </div>
                            </div>
                            <div class="col-sm-3">
                                <div id="wechatPicUploader">
                                    <div id="wechatPicFileList" style="position:relative;" class="uploader-list">
                                    </div>
                                    <div id="wechatPicFilePicker" class="FilePicker">上传微信图片</div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">详情页图片</label>
                            <div class="col-sm-3">
                                <div id="pcDetailPicUploader">
                                    <div id="pcDetailPicFileList" style="position:relative;" class="uploader-list">
                                    </div>
                                    <div id="pcDetailPicFilePicker" class="FilePicker">上传PC图片</div>
                                </div>
                            </div>
                            <div class="col-sm-3">
                                <div id="appDetailPicUploader">
                                    <div id="appDetailPicFileList" style="position:relative;" class="uploader-list">
                                    </div>
                                    <div id="appDetailPicFilePicker" class="FilePicker">上传APP图片</div>
                                </div>
                            </div>
                            <div class="col-sm-3">
                                <div id="wechatDetailPicUploader">
                                    <div id="wechatDetailPicFileList" style="position:relative;" class="uploader-list">
                                    </div>
                                    <div id="wechatDetailPicFilePicker" class="FilePicker">上传微信图片</div>
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

    <input type="hidden" id="pcimg" value="">
    <input type="hidden" id="appimg" value="">
    <input type="hidden" id="wechatimg" value="">
    <input type="hidden" id="pcdetailimg" value="">
    <input type="hidden" id="appdetailimg" value="">
    <input type="hidden" id="wechatdetailimg" value="">
    <input type="hidden" id="fileurl" value="">

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
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.zh-CN.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/select2/select2_locale_zh-CN.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/jquery-multi-select/js/jquery.multi-select.js"></script>
<script src="${request.contextPath}/static/js/webuploader-0.1.5/webuploader.js"></script>
<script src="${request.contextPath}/static/js/pointporduct.index.1.js?v=20210610"></script>
<script src="${request.contextPath}/static/js/webuploader.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/summernote/summernote.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/summernote/summernote-zh-CN.min.js"></script>
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

    $('#txtContent').summernote(optionsEdit);

    $('#txtRemark').summernote(optionsEdit);

    $('.datepicker').datetimepicker({
        language: 'zh-CN',
        format: 'yyyy-mm-dd hh:ii:00',
        minDate: 0
    });

    $('.select2').select2();

    function changeModal() {
        var optype = $("#productType option:selected").val();
        $("#exchange1")[0].checked=false;
        $("#exchange2")[0].checked=false;
        if (optype == "1") {
            $("#exchange2")[0].checked=true;
            $("#exchange_type input[type='radio']").attr("disabled", true);
        } else {
            $("#exchange1")[0].checked=true;
            $("#exchange_type input[type='radio']").attr("disabled", false);
        }
    }

    function editdata(id) {
        var jsondata = $('#json' + id).val();
        var res = JSON.parse(jsondata);

        $("#exchange1")[0].checked=false;
        $("#exchange2")[0].checked=false;

        $("#hiddenid").val(res.id);
        $("#productType").val(res.productType);
        $("#productDays").val(res.productDays);
        $("#actPid").val(res.activityPid);
        $("#statisticalClassification").val(res.statisticalClassification);
        if (res.userGroup) {
            $("#GroupList").val(res.userGroup.split(",")).trigger('change');
        } else {
            $("#GroupList").val("").trigger('change');
        }

        changeModal();

        if (res.exchangeType == 1) {
            $("#exchange2")[0].checked=true;
        } else {
            $("#exchange1")[0].checked=true;
        }
        $("#actCode").val(res.activityCode);
        if (res.activityStartTime) {
            $("#actStartTime").val(moment(res.activityStartTime).format("YYYY-MM-DD HH:mm:ss"));
        }
        if (res.activityEndTime) {
            $("#actEndTime").val(moment(res.activityEndTime).format("YYYY-MM-DD HH:mm:ss"));
        }
        $("#productName").val(res.productName);
        $("#productPrice").val(res.productPrice);
        $("#actPrice").val(res.activityPrice);
        $("#exChangeStartTime").val(moment(res.exchangeStarttime).format("YYYY-MM-DD HH:mm:ss"));
        $("#exChangeEndTime").val(moment(res.exchangeEndtime).format("YYYY-MM-DD HH:mm:ss"));
        $("#productCash").val(res.exchangeCash);
        $("#productPoint").val(res.exchangePoint);
        $("#totalLimit").val(res.totalLimit);
        $("#perLimit").val(res.perLimit);
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
        $("#pcimg").val(res.pcExangeimgurl);
        $("#appimg").val(res.appExangeimgurl);
        $("#wechatimg").val(res.webchatExangeimgurl);
        $("#pcdetailimg").val(res.pcExangeDetailimgurl);
        $("#appdetailimg").val(res.appExangeDetailimgurl);
        $("#wechatdetailimg").val(res.webchatExangeDetailimgurl);
        $("#fileurl").val(res.productFile);
        if (res.productFile != "" && res.productFile != null) {
            $("#FileList").html(res.productFile);
        }
        if (res.pcExangeimgurl != "" && res.pcExangeimgurl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.pcExangeimgurl + "\">" +
                "</div>";
            $("#pcPicFileList").html(imagestr);
        }
        if (res.appExangeimgurl != "" && res.appExangeimgurl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.appExangeimgurl + "\">" +
                "</div>";
            $("#appPicFileList").html(imagestr);
        }
        if (res.webchatExangeimgurl != "" && res.webchatExangeimgurl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.webchatExangeimgurl + "\">" +
                "</div>";
            $("#wechatPicFileList").html(imagestr);
        }
        if (res.pcExangeDetailimgurl != "" && res.pcExangeDetailimgurl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.pcExangeDetailimgurl + "\">" +
                "</div>";
            $("#pcDetailPicFileList").html(imagestr);
        }
        if (res.appExangeDetailimgurl != "" && res.appExangeDetailimgurl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.appExangeDetailimgurl + "\">" +
                "</div>";
            $("#appDetailPicFileList").html(imagestr);
        }
        if (res.webchatExangeDetailimgurl != "" && res.webchatExangeDetailimgurl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.webchatExangeDetailimgurl + "\">" +
                "</div>";
            $("#wechatDetailPicFileList").html(imagestr);
        }
        if (res.exchangeRemark) {
            $('#txtContent').summernote('code', res.exchangeRemark);
            //editor.txt.html(res.exchangeRemark);
            //UE.getEditor('txtContent').setContent(res.exchangeRemark);
        }
        if (res.remark) {
            $('#txtRemark').summernote('code', res.remark);
            //editor1.txt.html(res.remark);
            //UE.getEditor('txtContent').setContent(res.exchangeRemark);
        }

        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    }

    //删除
    function deleteproduct(id) {
        if (confirm("确认要删除吗？")) {
            var obj = new Object();
            obj.id = id;

            $.ajax({
                type: "POST",
                url: base_url + "/pointproduct/delete",
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
        $("#exchange1")[0].checked=false;
        $("#exchange2")[0].checked=false;
        $("#GroupList").val("").trigger('change');
        $("#actPid").val("");
        $("#hiddenid").val("");
        $("#productType").val("");
        $("#actCode").val("");
        $("#actStartTime").val("");
        $("#actEndTime").val("");
        $("#productName").val("");
        $("#productPrice").val("");
        $("#activityPrice").val("");
        $("#exChangeStartTime").val("");
        $("#exChangeEndTime").val("");
        $("#productCash").val("");
        $("#productPoint").val("");
        $("#totalLimit").val("");
        $("#perLimit").val("");
        $("#pcimg").val("");
        $("#appimg").val("");
        $("#wechatimg").val("");
        $("#pcdetailimg").val("");
        $("#appdetailimg").val("");
        $("#wechatdetailimg").val("");
        $("#pcPicFileList").html("");
        $("#appPicFileList").html("");
        $("#wechatPicFileList").html("");
        $("#pcDetailPicFileList").html("");
        $("#appDetailPicFileList").html("");
        $("#wechatDetailPicFileList").html("");
        $("#FileList").html("");
        $("#fileurl").val("");
        $("#statisticalClassification").val("");
        $('#txtContent').summernote('reset');
        $('#txtRemark').summernote('reset');
        //editor.txt.html("");
        //editor1.txt.html("");
        $("#modal-default").modal('hide');
    }

</script>

</body>
</html>
