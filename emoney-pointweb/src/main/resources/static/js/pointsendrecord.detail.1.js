$(function() {
    // init date tables
    var sendrecordDetailTable = $("#sendrecorddetail_list").DataTable({
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
            url: base_url + "/pointsendrecord/getPointSendRecordByBatchId",
            type:"post",
            data : function ( d ) {
                var obj = {};
                obj.batchId=$("#hiddenbatchId").val();
                obj.status=$("#opType").val();
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
                "data": 'remark'
            },
            {
                "data":'emNo'
            },
            {
                "data":'taskPoint'
            },
            {
                "data": 'createTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
            {
                "data":'sendStatus',
                "render": function (data, type, row) {
                    if(data){
                        if(data>0){
                            return "成功";
                        }else {
                            return "失败";
                        }
                    }else {
                        return "失败";
                    }
                }
            },
            {
                "data":'sendResult'
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
        sendrecordDetailTable.ajax.reload();
    });
})