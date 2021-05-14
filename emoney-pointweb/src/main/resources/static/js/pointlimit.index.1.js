$(function() {

    // init date tables
    var limitTable = $("#limit_list").DataTable({
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
            url: base_url + "/pointlimit/pageList",
            type:"post"
        },
        "searching": false,
        "ordering": false,
        "scrollX": true,
        "columns": [
            {
                "data": null
            },
            {
                "data": 'pointLimittype',
                "render": function (data, type, row) {
                    if(data)
                    {
                        switch (data)
                        {
                            case "1":
                                return "兑换";
                                break;
                            case "0":
                                return "发放";
                                break;
                            default:
                                return "发放";
                                break;
                        }
                    }
                    else {
                        return "";
                    }
                }
            },
            {
                "data": 'pointListto',
                "render": function (data, type, row) {
                    if(data)
                    {
                        switch (data)
                        {
                            case "1":
                                return "个人";
                                break;
                            case "0":
                                return "系统";
                                break;
                            default:
                                return "系统";
                                break;
                        }
                    }
                    else {
                        return "";
                    }
                }
            },
            {"data": 'pointLimitvalue'},
            {
                "data": 'updateTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
            {
                "data": 'updateBy'
            },
        ],
        fnDrawCallback: function () {
            let api = this.api();
            api.column(0).nodes().each(function(cell, i) {
                cell.innerHTML =  i + 1;
            });
        }
    });

})