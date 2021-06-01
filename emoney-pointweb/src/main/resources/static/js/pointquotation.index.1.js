$(function () {
    // init date tables
    var quotationTable = $("#quotation_list").DataTable({
        "processing": true,
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
            url: base_url + "/pointquotation/pageList",
            type: "post"
        },
        "searching": true,
        "ordering": true,
        "scrollX": true,
        "columns": [
            {
                "data": null
            },
            {
                "data": 'content'
            },
            {
                "data": 'showTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
            {
                "data": 'publishPlatFormType',
                "render": function (data, type, row) {
                    if (data) {
                        data = data.substring(0, data.length - 1).replace("1", "PC");
                        data = data.replace("2", "APP");
                        data = data.replace("3", "微信");
                        return data;
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": 'productVersion',
                "render": function (data, type, row) {
                    if (data) {
                        data = data.substring(0, data.length - 1).replace("888010000", "小智盈");
                        data = data.replace("888020000", "深度资金版");
                        data = data.replace("888080000", "掘金版");
                        data = data.replace("888010400", "小智盈过期");
                        data = data.replace("888020400", "大师过期");
                        return data;
                    } else {
                        return "";
                    }
                }
            },
            {
                "data": 'userGroupName',
                "render": function (data, type, row) {
                    if (data) {
                    }
                    return data;
                }
            },
            {
                "data": 'updateTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss") : "";
                }
            },
            {
                "data": 'updateBy'
            }
        ],
        fnDrawCallback: function () {
            let api = this.api();
            api.column(0).nodes().each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
        },
        columnDefs: [{
            targets: 8,
            render: function (data, type, row, meta) {
                var html = "<button type=\"button\" class=\"btn btn-primary btn-flat btn-sm\" onclick='editdata(" + row.id + ")'>编辑</button>";
                html += "<button type=\"button\" class=\"btn btn-danger btn-flat btn-sm\" onclick='deletedata(" + row.id + ")'>删除</button>";
                html += "<input type='hidden' id='json" + row.id + "' value='" + JSON.stringify(row) + "'>";
                return html;
            }
        }]
    });

    $('#btnAdd').on('click', function () {
        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    });

    //保存
    $(".btnSave").on("click", function () {
        var obj = new Object();
        obj.id = $("#hiddenid").val();
        obj.content = $("#content").val();
        obj.showTime = $("#showTime").val();
        var ver = "";
        $("#ver input[type=checkbox]:checked").each(function () {
            ver += $(this).val() + ',';
        })
        obj.ver = ver;
        var plat = "";
        $("#platfrom input[type=checkbox]:checked").each(function () {
            plat += $(this).val() + ',';
        })
        obj.plat = plat;
        var str = "";
        var goodsArr = $("#GroupList").select2("val");
        for (var i = 0; i < goodsArr.length; i++) {
            str += goodsArr[i];
            if (i + 1 < goodsArr.length) {
                str += ",";
            }
        }
        obj.groupList = str;
        if (!validate(obj)) {
            return false;
        }

        $.ajax({
            type: "POST",
            url: base_url + "/pointquotation/edit",
            data: obj,
            datatype: "text",
            success: function (data) {
                $(".btnSave").attr("disabled", false);
                if (data == "success") {
                    quotationTable.ajax.reload();
                    clertAndCloseModal();
                } else {
                    alert("保存失败");
                }
            },
            beforeSend: function () {
                $(".btnSave").attr("disabled", true);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    })

    function validate(obj) {
        if (!obj.content) {
            alert("请填写语录内容");
            return false;
        }
        if (!obj.ver) {
            alert("请勾选用户版本");
            return false;
        }
        if (!obj.plat) {
            alert("请勾选发布平台");
            return false;
        }
        return true;
    }

    $(".btnClose").on('click', function () {
        if (confirm("你的编辑未保存，确认要关闭吗？")) {
            clertAndCloseModal();
        }
    });

    function clertAndCloseModal() {
        $("#hiddenid").val("");
        $("#content").val("");
        $("#ver1").attr("checked", false);
        $("#ver2").attr("checked", false);
        $("#ver3").attr("checked", false);
        $("#ver4").attr("checked", false);
        $("#ver5").attr("checked", false);
        $("#plat1").attr("checked", false);
        $("#plat2").attr("checked", false);
        $("#plat3").attr("checked", false);
        $("#GroupList").val("").trigger('change');
        $("#showTime").val("");

        $("#modal-default").modal('hide');
    }
})