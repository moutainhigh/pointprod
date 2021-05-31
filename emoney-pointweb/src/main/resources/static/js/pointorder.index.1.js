$(function () {
    // init date tables
    var orderTable = $("#order_list").DataTable({
        "processing": true,
        //"serverSide": true,
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
            url: base_url + "/pointorder/pageList",
            type: "post",
            data: function (d) {
                var obj = {};
                obj.productType = $("#opType").val();
                obj.start = (d.start / d.length) + 1;
                obj.length = d.length;
                return obj;
            }
        },
        "searching": true,
        "ordering": true,
        "scrollX": true,
        "order": [[ 5, "desc" ]],
        "columns": [
            {
                "data": null
            },
            {
                "data": 'productTitle'
            },
            {
                "data": 'productType',
                "render": function (data, type, row) {
                    if (data) {
                        switch (data) {
                            case 1:
                                return "产品使用期";
                                break;
                            case 2:
                                return "优惠券";
                                break;
                            case 3:
                                return "新功能体验";
                                break;
                            case 4:
                                return "门票兑换";
                                break;
                            case 5:
                                return "实物兑换";
                                break;
                            default:
                                return "";
                                break;
                        }
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": 'emNo'
            },
            {
                'data': 'orderNo'
            },
            {
                "data": 'updateTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
            {
                'data': 'expressMobileMask'
            },
            {
                'data': 'expressMobile'
            },
            {
                'data': 'expressAddress'
            }
        ],
        fnDrawCallback: function (d) {
            let api = this.api();
            api.column(0).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        // fnDrawCallback: function (d) {
        //     let api = this.api();
        //     let startIndex = api.context[0]._iDisplayStart;//获取本页开始的条数
        //     api.column(0).nodes().each(function(cell, i) {
        //         cell.innerHTML = startIndex + i + 1;
        //     });
        // }
    });

    // search btn
    $('#opType').on('change', function () {
        orderTable.ajax.reload();
    });
})