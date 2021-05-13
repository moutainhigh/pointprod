$(function() {
    // init date tables
    var quotationTable = $("#quotation_list").DataTable({
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
            url: base_url + "/pointquotation/pageList",
            type:"post"
        },
        "searching": true,
        "ordering": true,
        "columns": [
            {
                "data": null
            },
            {
                "data": 'content'
            },
            {
                "data": 'createTime',
                "render": function (data, type, row) {
                    return data ? moment(new Date(data)).format("YYYY-MM-DD") : "";
                }
            },
            {
                "data":'updateBy'
            }
        ],
        fnDrawCallback: function () {
            let api = this.api();
            api.column(0).nodes().each(function(cell, i) {
                cell.innerHTML =  i + 1;
            });
        },
        columnDefs: [{
            targets: 4,
            render: function (data, type, row, meta) {
                var html = "<button type=\"button\" class=\"btn btn-primary btn-flat btn-sm\" onclick='editdata(" + row.id + ")'>编辑</button>";
                html += "<button type=\"button\" class=\"btn btn-danger btn-flat btn-sm\" onclick='deletedata(" + row.id + ")'>删除</button>";
                html += "<input type='hidden' id='json" + row.id + "' value='" + JSON.stringify(row) + "'>";
                return html;
            }
        }]
    });

    $('#btnAdd').on('click', function(){
        $("#modal-default").modal({ backdrop: false, keyboard: false }).modal('show');
    });

    //保存
    $(".btnSave").on("click",function (){
        var obj=new Object();
        obj.id=$("#hiddenid").val();
        obj.content=$("#content").val();

        if(!validate(obj)){
            return false;
        }

        $.ajax({
            type: "POST",
            url: base_url + "/pointquotation/edit",
            data: obj,
            datatype: "text",
            success: function (data) {
                $(".btnSave").attr("disabled",false);
                if (data == "success") {
                    quotationTable.ajax.reload();
                    clertAndCloseModal();
                }else {
                    alert("保存失败");
                }
            },
            beforeSend: function () {
                $(".btnSave").attr("disabled",true);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
            }
        });
    })

    function validate(obj){
        if(!obj.content){
            alert("请填写语录内容");
            return false;
        }
        return true;
    }

    $(".btnClose").on('click',function (){
        if(confirm("你的编辑未保存，确认要关闭吗？")){
            clertAndCloseModal();
        }
    });

    function clertAndCloseModal(){
        $("#hiddenid").val("");
        $("#content").val("");

        $("#modal-default").modal('hide');
    }
})