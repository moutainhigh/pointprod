$(function() {

    // init date tables
    var sendrecordTable = $("#sendrecord_list").DataTable({
        "processing" : true,
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
            url: base_url + "/pointsendrecord/pageList",
            type:"post",
            data : function ( d ) {
                var obj = {};
                obj.pointtype=$("#opType").val();
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
                "data": 'taskPoint',
                "render": function (data, type, row) {
                    if(data)
                    {
                        if(data>0){
                            return "赠送";
                        }else {
                            return "扣除";
                        }
                    }
                    else {
                        return "";
                    }
                }
            },
            {"data": 'taskName'},
            {
                "data": 'successCount',
                "render": function (data, type, row) {
                    return "<a href='" + base_url + "/pointsendrecord/pointSendRecordDetail?batchId=" + row.batchId + "'> " + data + "/" + (row.successCount+row.errorCount) + "</a>";
                }
            },
            {
                "data": 'remark'
            },
            {
                "data": 'createTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
            {
                "data": 'createBy'
            }
        ],
        fnDrawCallback: function () {
            let api = this.api();
            api.column(0).nodes().each(function(cell, i) {
                cell.innerHTML =  i + 1;
            });
        }
    });

    // search btn
    $('#opType').on('change', function(){
        sendrecordTable.ajax.reload();
    });

    changeType=function (){
        var data=$("#handleType input[type=radio]:checked").val();
        if(data=="1"){
            $("#excelTab").show();
            $("#inputTab").hide();
        }else {
            $("#excelTab").hide();
            $("#inputTab").show();
        }
    }

    $('#btnAdd').on('click', function(){
        changeType();
        $("#modal-default").modal({ backdrop: false, keyboard: false }).modal('show');
    });

    $(".btnClose").on('click',function (){
        clertAndCloseModal();
    });

    $(".btnSave").on("click",function (){
        var obj=new Object();
        obj.taskid=$("#task_type option:selected").val();
        obj.emNo=$("#EMtext").val();
        obj.remark=$("#remark").val();
        obj.file=$("#FilePicker").val();

        var postUrl=base_url + "/pointsendrecord/importUserData";
        $('.form').ajaxSubmit({
            beforeSubmit: function () {
                $(".btnSave").attr("disabled",true);
            },
            success: function (data) {
                $(".btnSave").attr("disabled",false);
                sendrecordTable.ajax.reload();
                clertAndCloseModal();
                if(data){
                    alert("成功" + data.success + "条，失败" + data.error + "条");
                }else {
                    alert("发放失败");
                }
            }
        });
    });

    function clertAndCloseModal(){
        $("#EMtext").val("");
        $("#remark").val("");
        $("#FilePicker").val("");
        $("#modal-default").modal('hide');
    }
})