$(function () {

    // init date tables
    var productTable = $("#product_list").DataTable({
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
            url: base_url + "/pointproduct/pageList",
            type: "post",
            data: function (d) {
                var obj = {};
                obj.productType = $("#opType").val();
                obj.productStatus = $("#opStatus").val();
                obj.ver = $("#opVer").val();
                obj.plat = $("#opPlat").val();
                return obj;
            }
        },
        "searching": true,
        "ordering": true,
        "scrollX": true,
        "columns": [
            {
                "data": 'id'
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
                                return "特色服务";
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
                "data": 'productName'
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
                "data": 'exchangeType',
                "render": function (data, type, row) {
                    if (data) {
                        if (data > 0) {
                            return "积分+现金";
                        } else {
                            return "积分";
                        }
                    } else {
                        return "积分";
                    }
                }
            },
            {
                "data": 'exchangePoint'
            },
            {
                "data": 'exchangeCash'
            },
            {
                "data": 'totalLimit'
            },
            {
                "data": 'perLimit'
            },
            {
                "data": 'exchangeStarttime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
            {
                "data": 'exchangeEndtime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
            {
                "data": 'updateBy'
            }
        ],
        columnDefs: [{
            targets: 12,
            render: function (data, type, row, meta) {
                var html = "<button type=\"button\" class=\"btn btn-primary btn-flat btn-sm\" onclick='editdata(" + row.id + ")'>编辑</button>";
                html += "<button type=\"button\" class=\"btn btn-danger btn-flat btn-sm\" onclick='deleteproduct(" + row.id + ")'>删除</button>";
                html += "<input type='hidden' id='json" + row.id + "' value='" + JSON.stringify(row) + "'>";
                return html;
            }
        }]
    });

    // search btn
    $('.op').on('change', function () {
        productTable.ajax.reload();
    });

    //显示活动图片配置
    $('#productType').on('change', function () {
        changeModal();
    });

    $('#btnAdd').on('click', function () {
        changeModal();

        $("#modal-default").modal({backdrop: false, keyboard: false}).modal('show');
    });

    //检索物流包信息
    $("#checkAcCode").on("click", function () {
        var obj = new Object();
        obj.acCode = $("#actCode").val();

        if (!obj.acCode) {
            alert("请输入物流包");
            return false;
        }

        var postUrl = base_url + "/pointproduct/checkActivityCode";
        $.ajax({
            type: "POST",
            url: postUrl,
            data: obj,
            success: function (data) {
                if (data.code == "0" && data.data[0] != null) {
                    $("#productName").val(data.data[0].ActivityName);
                    $("#productPrice").val(data.data[0].ActivityPrice);
                    $("#actPrice").val(data.data[0].ActivityPrice);
                    $("#actStartTime").val(moment(data.data[0].ActivityStartTime).format("YYYY-MM-DD HH:mm:ss"))
                    $("#actEndTime").val(moment(data.data[0].ActivityEndTime).format("YYYY-MM-DD HH:mm:ss"))
                    $("#actPid").val(data.data[0].children[0].ProductID);
                } else {
                    alert("未查询到物流包信息");
                }
            },
            beforeSend: function () {
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });

        return false;
    });

    //保存
    $(".btnSave").on("click", function () {
        var obj = new Object();
        obj.id = $("#hiddenid").val();
        obj.productType = $("#productType option:selected").val();
        obj.exchangeType = $("#exchange_type input[type=radio]:checked").val();
        obj.acCode = $("#actCode").val();
        obj.productDays = $("#productDays").val();
        obj.actStartTime = $("#actStartTime").val();
        obj.actEndTime = $("#actEndTime").val();
        obj.productName = $("#productName").val();
        obj.productPrice = $("#productPrice").val();
        obj.activityPrice = $("#actPrice").val();
        obj.exChangeStartTime = $("#exChangeStartTime").val();
        obj.exChangeEndTime = $("#exChangeEndTime").val();
        obj.productCash = $("#productCash").val();
        obj.productPoint = $("#productPoint").val();
        obj.totalLimit = $("#totalLimit").val();
        obj.perLimit = $("#perLimit").val();
        obj.fileurl = $("#fileurl").val();
        obj.Pid = $("#actPid").val();
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
        obj.exChangeContent = editor.txt.html();
        obj.remark = editor1.txt.html();
        obj.pcimg = $("#pcimg").val();
        obj.appimg = $("#appimg").val();
        obj.wechatimg = $("#wechatimg").val();
        obj.pcdetailimg = $("#pcdetailimg").val();
        obj.appdetailimg = $("#appdetailimg").val();
        obj.wechatdetailimg = $("#wechatdetailimg").val();

        if (!validate(obj)) {
            return false;
        }

        $.ajax({
            type: "POST",
            url: base_url + "/pointproduct/edit",
            data: obj,
            datatype: "text",
            success: function (data) {
                $(".btnSave").attr("disabled", false);
                if (data == "success") {
                    productTable.ajax.reload();
                    clertAndCloseModal();
                } else {
                    alert(data);
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
        if (!obj.exChangeStartTime) {
            alert("请选择兑换开始时间");
            return false;
        }
        if (!obj.exChangeEndTime) {
            alert("请选择兑换结束时间");
            return false;
        }
        if (!obj.exchangeType) {
            alert("请选择兑换类型");
            return false;
        }
        if (!obj.productPoint) {
            alert("请填写积分数量");
            return false;
        }
        if (!obj.totalLimit) {
            alert("请填写总限额");
            return false;
        }
        if (!obj.perLimit) {
            alert("请填写每人限额");
            return false;
        }
        if (!obj.ver) {
            alert("请选择产品版本");
            return false;
        }
        return true;
    }

    $(".btnClose").on('click', function () {
        if (confirm("你的编辑未保存，确认要关闭吗？")) {
            clertAndCloseModal();
        }
    });
})