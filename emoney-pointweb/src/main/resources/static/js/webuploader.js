$(function() {

    // 优化retina, 在retina下这个值是2
    ratio = window.devicePixelRatio || 1;

    // 缩略图大小
    thumbnailWidth = 100 * ratio;
    thumbnailHeight = 100 * ratio;

    // pc
    var $pcPiclist = $('#pcPicFileList'),

    // Web Uploader实例
    pcuploader;

    var pcuploader = WebUploader.create({

        auto: true,

        // swf文件路径
        swf: base_url + '/static/js/webuploader-0.1.5/webuploader.swf',

        // 文件接收服务端。
        server: base_url+'/fileuploader/upload',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#pcPicFilePicker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,

        // 只允许选择图片文件。
        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        },

        // 上传图片时的参数
        formData: {
            fileType: "image"
        },

        duplicate: true
    });

    // 当有文件添加进来的时候
    pcuploader.on( 'fileQueued', function( file ) {
        $pcPiclist.find(".file-item").remove();
        var $li = $(
            '<div id="' + file.id + '" class="file-item thumbnail">' +
            '<img>' +
            '<div class="info">' + file.name + '</div>' +
            '</div>'
            ),
            $img = $li.find('img');


        // $list为容器jQuery实例
        $pcPiclist.append( $li );

        // 创建缩略图
        // 如果为非图片文件，可以不用调用此方法。
        // thumbnailWidth x thumbnailHeight 为 100 x 100
        pcuploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }

            $img.attr( 'src', src );
        }, thumbnailWidth, thumbnailHeight );
    });

    // 文件上传成功，给item添加成功class, 用样式标记上传成功。
    pcuploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).addClass('upload-state-done');
        $("#pcimg").val(response.url);
        setTimeout(function () {
            $pcPiclist.find(".file-item img").eq(0).attr("src", response.url);
        }, 300);
    });

    // 文件上传失败，现实上传出错。
    pcuploader.on('uploadError', function (file) {
        var $li = $('#' + file.id),
            $error = $li.find('div.error');

        // 避免重复创建
        if (!$error.length) {
            $error = $('<div class="error"></div>').appendTo($li);
        }

        $error.text('上传失败');
    });

    //app
    var $appPiclist = $('#appPicFileList'),

        // Web Uploader实例
        appuploader;

    var appuploader = WebUploader.create({

        auto: true,

        // swf文件路径
        swf: base_url + '/static/js/webuploader-0.1.5/webuploader.swf',

        // 文件接收服务端。
        server: base_url+'/fileuploader/upload',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#appPicFilePicker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,

        // 只允许选择图片文件。
        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        },

        // 上传图片时的参数
        formData: {
            fileType: "image"
        },

        duplicate: true
    });

    // 当有文件添加进来的时候
    appuploader.on( 'fileQueued', function( file ) {
        $appPiclist.find(".file-item").remove();
        var $li = $(
            '<div id="' + file.id + '" class="file-item thumbnail">' +
            '<img>' +
            '<div class="info">' + file.name + '</div>' +
            '</div>'
            ),
            $img = $li.find('img');


        // $list为容器jQuery实例
        $appPiclist.append( $li );

        // 创建缩略图
        // 如果为非图片文件，可以不用调用此方法。
        // thumbnailWidth x thumbnailHeight 为 100 x 100
        appuploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }

            $img.attr( 'src', src );
        }, thumbnailWidth, thumbnailHeight );
    });

    // 文件上传成功，给item添加成功class, 用样式标记上传成功。
    appuploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).addClass('upload-state-done');
        $("#appimg").val(response.url);
        setTimeout(function () {
            $appPiclist.find(".file-item img").eq(0).attr("src", response.url);
        }, 300);
    });

    // 文件上传失败，现实上传出错。
    appuploader.on('uploadError', function (file) {
        var $li = $('#' + file.id),
            $error = $li.find('div.error');

        // 避免重复创建
        if (!$error.length) {
            $error = $('<div class="error"></div>').appendTo($li);
        }

        $error.text('上传失败');
    });

    //wechat
    var $wechatPiclist = $('#wechatPicFileList'),

        // Web Uploader实例
        wechatuploader;

    var wechatuploader = WebUploader.create({

        auto: true,

        // swf文件路径
        swf: base_url + '/static/js/webuploader-0.1.5/webuploader.swf',

        // 文件接收服务端。
        server: base_url+'/fileuploader/upload',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#wechatPicFilePicker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,

        // 只允许选择图片文件。
        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        },

        // 上传图片时的参数
        formData: {
            fileType: "image"
        },

        duplicate: true
    });

    // 当有文件添加进来的时候
    wechatuploader.on( 'fileQueued', function( file ) {
        $wechatPiclist.find(".file-item").remove();
        var $li = $(
            '<div id="' + file.id + '" class="file-item thumbnail">' +
            '<img>' +
            '<div class="info">' + file.name + '</div>' +
            '</div>'
            ),
            $img = $li.find('img');


        // $list为容器jQuery实例
        $wechatPiclist.append( $li );

        // 创建缩略图
        // 如果为非图片文件，可以不用调用此方法。
        // thumbnailWidth x thumbnailHeight 为 100 x 100
        wechatuploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }

            $img.attr( 'src', src );
        }, thumbnailWidth, thumbnailHeight );
    });

    // 文件上传成功，给item添加成功class, 用样式标记上传成功。
    wechatuploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).addClass('upload-state-done');
        $("#wechatimg").val(response.url);
        setTimeout(function () {
            $wechatPiclist.find(".file-item img").eq(0).attr("src", response.url);
        }, 300);
    });

    // 文件上传失败，现实上传出错。
    wechatuploader.on('uploadError', function (file) {
        var $li = $('#' + file.id),
            $error = $li.find('div.error');

        // 避免重复创建
        if (!$error.length) {
            $error = $('<div class="error"></div>').appendTo($li);
        }

        $error.text('上传失败');
    });


    // pc
    var $pcDetailPiclist = $('#pcDetailPicFileList'),

        // Web Uploader实例
        pcDetailuploader;

    var pcDetailuploader = WebUploader.create({

        auto: true,

        // swf文件路径
        swf: base_url + '/static/js/webuploader-0.1.5/webuploader.swf',

        // 文件接收服务端。
        server: base_url+'/fileuploader/upload',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#pcDetailPicFilePicker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,

        // 只允许选择图片文件。
        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        },

        // 上传图片时的参数
        formData: {
            fileType: "image"
        },

        duplicate: true
    });

    // 当有文件添加进来的时候
    pcDetailuploader.on( 'fileQueued', function( file ) {
        $pcDetailPiclist.find(".file-item").remove();
        var $li = $(
            '<div id="' + file.id + '" class="file-item thumbnail">' +
            '<img>' +
            '<div class="info">' + file.name + '</div>' +
            '</div>'
            ),
            $img = $li.find('img');


        // $list为容器jQuery实例
        $pcDetailPiclist.append( $li );

        // 创建缩略图
        // 如果为非图片文件，可以不用调用此方法。
        // thumbnailWidth x thumbnailHeight 为 100 x 100
        pcDetailuploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }

            $img.attr( 'src', src );
        }, thumbnailWidth, thumbnailHeight );
    });

    // 文件上传成功，给item添加成功class, 用样式标记上传成功。
    pcDetailuploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).addClass('upload-state-done');
        $("#pcdetailimg").val(response.url);
        setTimeout(function () {
            $pcDetailPiclist.find(".file-item img").eq(0).attr("src", response.url);
        }, 300);
    });

    // 文件上传失败，现实上传出错。
    pcDetailuploader.on('uploadError', function (file) {
        var $li = $('#' + file.id),
            $error = $li.find('div.error');

        // 避免重复创建
        if (!$error.length) {
            $error = $('<div class="error"></div>').appendTo($li);
        }

        $error.text('上传失败');
    });

    //app
    var $appDetailPiclist = $('#appDetailPicFileList'),

        // Web Uploader实例
        appDetailuploader;

    var appDetailuploader = WebUploader.create({

        auto: true,

        // swf文件路径
        swf: base_url + '/static/js/webuploader-0.1.5/webuploader.swf',

        // 文件接收服务端。
        server: base_url+'/fileuploader/upload',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#appDetailPicFilePicker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,

        // 只允许选择图片文件。
        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        },

        // 上传图片时的参数
        formData: {
            fileType: "image"
        },

        duplicate: true
    });

    // 当有文件添加进来的时候
    appDetailuploader.on( 'fileQueued', function( file ) {
        $appDetailPiclist.find(".file-item").remove();
        var $li = $(
            '<div id="' + file.id + '" class="file-item thumbnail">' +
            '<img>' +
            '<div class="info">' + file.name + '</div>' +
            '</div>'
            ),
            $img = $li.find('img');


        // $list为容器jQuery实例
        $appDetailPiclist.append( $li );

        // 创建缩略图
        // 如果为非图片文件，可以不用调用此方法。
        // thumbnailWidth x thumbnailHeight 为 100 x 100
        appDetailuploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }

            $img.attr( 'src', src );
        }, thumbnailWidth, thumbnailHeight );
    });

    // 文件上传成功，给item添加成功class, 用样式标记上传成功。
    appDetailuploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).addClass('upload-state-done');
        $("#appdetailimg").val(response.url);
        setTimeout(function () {
            $appDetailPiclist.find(".file-item img").eq(0).attr("src", response.url);
        }, 300);
    });

    // 文件上传失败，现实上传出错。
    appDetailuploader.on('uploadError', function (file) {
        var $li = $('#' + file.id),
            $error = $li.find('div.error');

        // 避免重复创建
        if (!$error.length) {
            $error = $('<div class="error"></div>').appendTo($li);
        }

        $error.text('上传失败');
    });

    //wechat
    var $wechatDetailPiclist = $('#wechatDetailPicFileList'),

        // Web Uploader实例
        wechatDetailuploader;

    var wechatDetailuploader = WebUploader.create({

        auto: true,

        // swf文件路径
        swf: base_url + '/static/js/webuploader-0.1.5/webuploader.swf',

        // 文件接收服务端。
        server: base_url+'/fileuploader/upload',

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#wechatDetailPicFilePicker',

        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,

        // 只允许选择图片文件。
        accept: {
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        },

        // 上传图片时的参数
        formData: {
            fileType: "image"
        },

        duplicate: true
    });

    // 当有文件添加进来的时候
    wechatDetailuploader.on( 'fileQueued', function( file ) {
        $wechatDetailPiclist.find(".file-item").remove();
        var $li = $(
            '<div id="' + file.id + '" class="file-item thumbnail">' +
            '<img>' +
            '<div class="info">' + file.name + '</div>' +
            '</div>'
            ),
            $img = $li.find('img');


        // $list为容器jQuery实例
        $wechatDetailPiclist.append( $li );

        // 创建缩略图
        // 如果为非图片文件，可以不用调用此方法。
        // thumbnailWidth x thumbnailHeight 为 100 x 100
        wechatDetailuploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }

            $img.attr( 'src', src );
        }, thumbnailWidth, thumbnailHeight );
    });

    // 文件上传成功，给item添加成功class, 用样式标记上传成功。
    wechatDetailuploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).addClass('upload-state-done');
        $("#wechatdetailimg").val(response.url);
        setTimeout(function () {
            $wechatDetailPiclist.find(".file-item img").eq(0).attr("src", response.url);
        }, 300);
    });

    // 文件上传失败，现实上传出错。
    wechatDetailuploader.on('uploadError', function (file) {
        var $li = $('#' + file.id),
            $error = $li.find('div.error');

        // 避免重复创建
        if (!$error.length) {
            $error = $('<div class="error"></div>').appendTo($li);
        }

        $error.text('上传失败');
    });
});