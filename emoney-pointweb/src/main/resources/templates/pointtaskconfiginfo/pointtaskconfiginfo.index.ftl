<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <#import "../common/common.macro.ftl" as netCommon>
    <@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-daterangepicker/daterangepicker.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap-timepicker/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/select2/select2.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/jquery-multi-select/css/multi-select.css">
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
    <title>积分任务配置</title>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "pointtaskconfiginfo" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>积分任务配置</h1>
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
                            <label style="float:left;margin-bottom:2px;margin-top:10px;margin-left:6px;">任务类型：</label>
                            <select id="opType" class="form-control" style="float:left;width:150px;margin-top:5px;">
                                <option value="0">全部</option>
                                <option value="1">新手任务</option>
                                <option value="2">成长任务</option>
                                <option value="3">贡献任务</option>
                                <option value="4">活动任务</option>
                                <option value="5">其他</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12">
                    <div class="box">
                        <div class="box-body" >
                            <table id="task_list" class="table table-bordered table-striped">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>任务ID</th>
                                    <th>任务类型</th>
                                    <th title="序号越大越靠前">排序</th>
                                    <th>任务名称</th>
                                    <th>获得积分</th>
                                    <th>是否每日任务</th>
                                    <th>是否定向</th>
                                    <th>参与次数</th>
                                    <th>开始时间</th>
                                    <th>结束时间</th>
                                    <th>任务发布平台</th>
                                    <th>产品版本</th>
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
                    <form class="form-horizontal form" role="form" >
                        <input type="hidden" id="hiddenid" value="">
                        <input type="hidden" id="hiddentaskId" value="">
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">任务Id<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <label for="firstname" class="control-label" id="taskId"></label>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">立即发送<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="sendType">
                                    <input id="send1" type="radio" name="send" value="1" /><label for="send1">是</label>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input id="send2" type="radio" name="send" value="0" checked/><label for="send2">否</label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">任务类型<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <select class="form-control" name="task_type" id="task_type">
                                    <option value="1">新手任务</option>
                                    <option value="2">成长任务</option>
                                    <option value="3">贡献任务</option>
                                    <option value="4">活动任务</option>
                                    <option value="5">其他</option>
                                </select>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">子任务Id</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="subId" id="subId" placeholder="子任务Id" >
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">任务名称<font color="red">*</font></label>
                            <div class="col-sm-10">
                                <input type="text" class="form-control" name="task_name" id="task_name" placeholder="任务名称" maxlength="50" >
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">获取积分<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="task_points" id="task_points" placeholder="获取积分" maxlength="50" >
                            </div>
                            <div id="isshow" style="display: none;">
                            <label for="lastname" class="col-sm-2 control-label">首页显示<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="isHomePage">
                                    <input id="show1" type="radio" name="show" value="1" /><label for="show1">是</label>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input id="show2" type="radio" name="show" value="0" checked/><label for="show2">否</label>
                                </div>
                            </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">每日任务<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="daily">
                                    <input id="daily1" type="radio" name="daily" value="1" checked /><label for="daily1">是</label>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input id="daily2" type="radio" name="daily" value="0" /><label for="daily2">否</label>
                                </div>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">参与次数<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" name="jobDesc" id="jointimes" placeholder="参与次数" maxlength="50" >
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">产品版本<font color="red">*</font></label>
                            <div class="col-sm-8">
                                <div style="margin-top: 6px;" id="ver">
                                    <input id="ver1" type="checkbox" name="ver" value="888010000" /><label for="ver1">小智盈</label>
                                    <input id="ver2" type="checkbox" name="ver" value="888020000" /><label for="ver2">深度资金版</label>
                                    <input id="ver3" type="checkbox" name="ver" value="888080000" /><label for="ver3">掘金版</label>
                                    <input id="ver4" type="checkbox" name="ver" value="888010400" /><label for="ver3">小智盈过期</label>
                                    <input id="ver5" type="checkbox" name="ver" value="888020400" /><label for="ver3">大师过期</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">是否定向<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="Directional">
                                    <input id="directional1" type="radio" name="role" value="1" /><label for="directional1">是</label>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input id="directional2" type="radio" name="role" value="0" checked/><label for="directional2">否</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">发布平台<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="platfrom">
                                    <input id="plat1" type="checkbox" name="ver" value="1" /><label for="plat1">PC</label>
                                    <input id="plat2" type="checkbox" name="ver" value="2" /><label for="plat2">APP</label>
                                    <input id="plat3" type="checkbox" name="ver" value="3" /><label for="plat3">微信</label>
                                </div>
                            </div>
                            <label for="lastname" class="col-sm-2 control-label">显示顺序<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" id="taskorder" placeholder="显示顺序" maxlength="50" >
                            </div>
                        </div>

                        <div class="form-group">
                            <!--实际为激活时间，需求强烈要求修改为登录-->
                            <label for="firstname" class="col-sm-2 control-label">任务时间<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <input type="text" class="form-control pull-right datepicker" id="starttime">
                                </div>
                            </div>
                            <div class="col-sm-4">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <input type="text" class="form-control pull-right datepicker" id="endtime">
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">用户分组</label>
                            <div class="col-sm-10">
                                <select id="GroupList" class="select2" multiple="multiple" title="请选择" style="width: 100%;">
                                    <#if userGroupVOList?exists && userGroupVOList?size gt 0 >
                                        <#list userGroupVOList as item>
                                            <option value="${item.id}">${item.userGroupName}</option>
                                        </#list>
                                    </#if>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">跳转地址</label>
                            <div class="col-sm-10">
                                <div style="margin-top: 6px;">
                                    <input type="text" class="form-control" name="jobDesc" id="pcurl" placeholder="PC跳转地址" >
                                </div>
                                <div style="margin-top: 6px;">
                                    <input type="text" class="form-control" name="jobDesc" id="appurl" placeholder="APP地址" >
                                </div>
                                <div style="margin-top: 6px;">
                                    <input type="text" class="form-control" name="jobDesc" id="wechaturl" placeholder="微信跳转地址" >
                                </div>
                            </div>
                        </div>

                        <div class="form-group" style="z-index: 1">
                            <label for="firstname" class="col-sm-2 control-label">任务说明</label>
                            <div class="col-sm-10">
                                <div id="taskRemark"></div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="firstname" class="col-sm-2 control-label">按钮文案</label>
                            <div class="col-sm-4">
                                <input type="text" class="form-control" id="buttontext" placeholder="按钮文案" maxlength="50" >
                            </div>
                        </div>

                        <div class="form-group" id="imgtype" style="display: none;">
                            <label for="lastname" class="col-sm-2 control-label">图片大小<font color="red">*</font></label>
                            <div class="col-sm-4">
                                <div style="margin-top: 6px;" id="isBigImg">
                                    <input id="big" type="radio" name="imgsize" value="1" /><label for="big">大图</label>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input id="small" type="radio" name="imgsize" value="0" checked/><label for="small">小图</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group" id="ImgList" style="display: none;">
                            <label for="firstname" class="col-sm-2 control-label">图片</label>
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
                    </form>
                </div>
                <div class="modal-footer alipay wechatpay">
                    <button type="button" class="btn btn-default btnClose">关闭</button>
                    <button type="button" class="btn btn-primary btnSave">保存</button>
                </div>
            </div>
        </div>
    </div>


    <input type="hidden" id="pcimg" value="">
    <input type="hidden" id="appimg" value="">
    <input type="hidden" id="wechatimg" value="">
    <!-- footer -->
    <@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!--富文本编辑框-->
<script src="${request.contextPath}/static/js/wangEditor.min.js"></script>
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
<script src="${request.contextPath}/static/js/pointtaskconfiginfo.index.1.js?v=1111"></script>
<script src="${request.contextPath}/static/js/webuploader.js"></script>

<script>

    const E = window.wangEditor
    const editor = new E('#taskRemark')
    // 配置菜单栏，删减菜单，调整顺序
    editor.config.menus = [
        'foreColor',
        'fontName',
        'fontSize',
        'lineHeight',
        'bold',
        'head',
        'link',
        'italic',
        'underline'
    ]
    // 配置字体
    editor.config.fontNames = [
        '黑体',
        '仿宋',
        '楷体',
        '标楷体',
        '华文仿宋',
        '华文楷体',
        '宋体',
        '微软雅黑',
        'Arial',
        'Tahoma',
        'Verdana',
        'Times New Roman',
        'Courier New',
    ]
    editor.config.placeholder = '请输入内容';
    editor.create()

    $('.datepicker').datetimepicker({
        language: 'zh-CN',
        format: 'yyyy-mm-dd hh:ii:00',
        minDate: 0
    });

    $('.select2').select2();

    function editdata(id){
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
        $("#send1").attr("checked", false);
        $("#send2").attr("checked", false);
        $("#show1").attr("checked", false);
        $("#show2").attr("checked", false);
        $("#directional1").attr("checked", false);
        $("#directional2").attr("checked", false);
        $("#daily1").attr("checked", false);
        $("#daily2").attr("checked", false);

        var obj=new Object();
        obj.taskId=res.taskId;
        obj.subId=res.subId;
        $.ajax({
            type: "POST",
            url: base_url + "/pointtaskconfiginfo/checkTaskStatus",
            data: obj,
            datatype: "text",
            success: function (data) {
                if(data){
                    $("#task_type").attr("disabled",true);
                    $("#task_points").attr("disabled",true);
                    $("#tasktime").attr("disabled",true);
                    $("#activation").attr("disabled",true);
                    $("#expire").attr("disabled",true);
                    $("#jointimes").attr("disabled",true);
                    $(".form input[type='radio']").attr("disabled",true);
                    $(".form input[type='checkbox']").attr("disabled",true);
                    $("#subId").attr("disabled",true);
                }else {
                    $("#task_type").attr("disabled",false);
                    $("#task_points").attr("disabled",false);
                    $("#tasktime").attr("disabled",false);
                    $("#activation").attr("disabled",false);
                    $("#expire").attr("disabled",false);
                    $("#jointimes").attr("disabled",false);
                    $(".form input[type='radio']").attr("disabled",false);
                    $(".form input[type='checkbox']").attr("disabled",false);
                    $(".cleartime").removeClass("disable");
                    $("#subId").attr("disabled",false);
                }
            },
            beforeSend: function () {
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });

        $("#hiddenid").val(res.id);
        $("#hiddentaskId").val(res.taskId);
        $("#taskId").html(res.taskId);
        $("#subId").val(res.subId);
        $("#task_type").val(res.taskType);
        $("#task_name").val(res.taskName);
        $("#task_points").val(res.taskPoints);
        if(res.userGroup){
            $("#GroupList").val(res.userGroup.split(",")).trigger('change');
        }else {
            $("#GroupList").val("").trigger('change');
        }
        if(res.taskStartTime){
            $("#starttime").val(moment(res.taskStartTime).format("YYYY-MM-DD HH:mm:ss"));
        }
        if(res.taskEndTime){
            $("#endtime").val(moment(res.taskEndTime).format("YYYY-MM-DD HH:mm:ss"));
        }

        if(res.activationStartTime){
            $("#activationstarttime").val(moment(res.activationStartTime).format("YYYY-MM-DD HH:mm:ss"));
        }
        if(res.activationEndTime){
            $("#activationendtime").val(moment(res.activationEndTime).format("YYYY-MM-DD HH:mm:ss"));
        }

        if(res.expireStartTime){
            $("#expirestarttime").val(moment(res.expireStartTime).format("YYYY-MM-DD HH:mm:ss"));
        }
        if(res.expireEndTime){
            $("#expireendtime").val(moment(res.expireEndTime).format("YYYY-MM-DD HH:mm:ss"));
        }

        if(res.isDirectional){
            $("#directional1").attr("checked", true);
        }
        else {
            $("#directional2").attr("checked", true);
        }

        if(res.sendType){
            $("#send1").attr("checked", true);
        }else {
            $("#send2").attr("checked", true);
        }

        if(res.isDailyTask){
            $("#daily1").attr("checked", true);
        }else {
            $("#daily2").attr("checked", true);
        }

        $("#jointimes").val(res.dailyJoinTimes);

        if(res.productVersion){
            var ver=res.productVersion.split(',');
            for (var i=0;i<ver.length;i++){
                if(ver[i]=="888010000"){
                    $("#ver1").attr("checked", true);
                }
                if(ver[i]=="888020000"){
                    $("#ver2").attr("checked", true);
                }
                if(ver[i]=="888080000"){
                    $("#ver3").attr("checked", true);
                }
                if(ver[i]=="888010400"){
                    $("#ver4").attr("checked", true);
                }
                if(ver[i]=="888020400"){
                    $("#ver5").attr("checked", true);
                }
            }
        }

        if(res.publishPlatFormType){
            var plat=res.publishPlatFormType.split(',');
            for (var i=0;i<plat.length;i++){
                if(plat[i]==1){
                    $("#plat1").attr("checked", true);
                }
                if(plat[i]==2){
                    $("#plat2").attr("checked", true);
                }
                if(plat[i]==3){
                    $("#plat3").attr("checked", true);
                }
            }
        }

        if(res.isShowInHomePage){
            if(res.isShowInHomePage==1){
                $("#show1").attr("checked", true);
            }else {
                $("#show2").attr("checked", true);
            }
        }

        if(res.isBigImg){
            $("#big").attr("checked", true);
        }else {
            $("#small").attr("checked", true);
        }

        $("#taskorder").val(res.taskOrder);
        $("#buttontext").val(res.taskButtonText);
        $("#pcurl").val(res.pcRedirectUrl);
        $("#appurl").val(res.appRedirectUrl);
        $("#wechaturl").val(res.wechatRedirectUrl);

        if(res.taskType==4){
            $("#imgtype").show();
            $("#isshow").show();
            $("#ImgList").show();
        }else {
            $("#imgtype").hide();
            $("#isshow").hide();
            $("#ImgList").hide();
        }
        $("#pcimg").val(res.pcTaskImgUrl);
        $("#appimg").val(res.appTaskImgUrl);
        $("#wechatimg").val(res.wechatTaskImgUrl);
        if (res.pcTaskImgUrl != "" && res.pcTaskImgUrl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.pcTaskImgUrl + "\">" +
                "</div>";
            $("#pcPicFileList").html(imagestr);
        }
        if (res.appTaskImgUrl != "" && res.pcTaskImgUrl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.appTaskImgUrl + "\">" +
                "</div>";
            $("#pcPicFileList").html(imagestr);
        }
        if (res.wechatTaskImgUrl != "" && res.pcTaskImgUrl != null) {
            var imagestr = "<div class=\"file-item thumbnail upload-state-done oldImage\">" +
                "<img src=\"" + res.wechatTaskImgUrl + "\">" +
                "</div>";
            $("#pcPicFileList").html(imagestr);
        }
        if(res.taskRemark){
            editor.txt.html(res.taskRemark);
        }

        $("#modal-default").modal({ backdrop: false, keyboard: false }).modal('show');
    }

    function copytask(id){
        var jsondata = $('#json' + id).val();
        var res = JSON.parse(jsondata);

        $("#hiddentaskId").val(res.taskId);
        $("#taskId").html(res.taskId);

        $("#ver1").attr("checked", false);
        $("#ver2").attr("checked", false);
        $("#ver3").attr("checked", false);
        $("#ver4").attr("checked", false);
        $("#ver5").attr("checked", false);
        $("#plat1").attr("checked", false);
        $("#plat2").attr("checked", false);
        $("#plat3").attr("checked", false);
        $("#show1").attr("checked", false);
        $("#show2").attr("checked", false);
        $("#send1").attr("checked", false);
        $("#send2").attr("checked", false);
        $("#directional1").attr("checked", false);
        $("#directional2").attr("checked", false);
        $("#daily1").attr("checked", false);
        $("#daily2").attr("checked", false);
        $("#task_type").attr("disabled",false);
        $("#task_points").attr("disabled",false);
        $("#tasktime").attr("disabled",false);
        $("#activation").attr("disabled",false);
        $("#expire").attr("disabled",false);
        $("#taskorder").attr("disabled",false);
        $("#pcurl").attr("disabled",false);
        $("#appurl").attr("disabled",false);
        $("#wechaturl").attr("disabled",false);
        $("#jointimes").attr("disabled",false);
        $(".form input[type='radio']").attr("disabled",false);
        $(".form input[type='checkbox']").attr("disabled",false);
        $(".cleartime").removeClass("disable");

        $("#modal-default").modal({ backdrop: false, keyboard: false }).modal('show');
    }

</script>
</body>
</html>
