<#macro commonStyle>

<#-- favicon -->
    <link rel="icon" href="${request.contextPath}/static/favicon.ico" />

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/font-awesome/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/Ionicons/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/skins/_all-skins.min.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <!-- pace -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/PACE/themes/blue/pace-theme-flash.css">

</#macro>

<#macro commonScript>
    <!-- jQuery 2.1.4 -->
    <script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="${request.contextPath}/static/adminlte/bower_components/bootstrap/js/bootstrap.min.js"></script>
    <!-- FastClick -->
    <script src="${request.contextPath}/static/adminlte/bower_components/fastclick/fastclick.js"></script>
    <!-- AdminLTE App -->
    <script src="${request.contextPath}/static/adminlte/dist/js/adminlte.min.js"></script>
    <!-- jquery.slimscroll -->
    <script src="${request.contextPath}/static/adminlte/bower_components/jquery-slimscroll/jquery.slimscroll.min.js"></script>

    <!-- pace -->
    <script src="${request.contextPath}/static/adminlte/bower_components/PACE/pace.min.js"></script>

    <script>
        var base_url = '${request.contextPath}';
    </script>

</#macro>

<#macro commonHeader>
    <header class="main-header">
        <a href="${request.contextPath}/" class="logo">
            <span class="logo-mini"><b></b></span>
            <span class="logo-lg"><b>积分后台</b></span>
        </a>
        <nav class="navbar navbar-static-top" role="navigation">

            <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>

            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <#-- login user -->
                    <li class="dropdown">
                        <a href="javascript:" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                            ${Request["username"]}
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu" role="menu">
                            <li id="logoutBtn" ><a href="/logout">退出</a></li>
                        </ul>
                    </li>
                </ul>
            </div>

        </nav>
    </header>
</#macro>

<#macro commonLeft pageName >
    <aside class="main-sidebar">
        <section class="sidebar">
            <ul class="sidebar-menu">
                <li class="header">导航</li>
                <li class="nav-click <#if pageName == "pointtaskconfiginfo">active</#if>" ><a href="/pointtaskconfiginfo"><i class="fa fa-circle-o text-yellow"></i><span>任务配置</span></a></li>
                <li class="nav-click <#if pageName == "pointlimit">active</#if>" ><a href="/pointlimit"><i class="fa fa-circle-o text-yellow"></i><span>限额配置</span></a></li>
                <li class="nav-click <#if pageName == "pointsendrecord">active</#if>" ><a href="/pointsendrecord"><i class="fa fa-circle-o text-yellow"></i><span>积分调整</span></a></li>
                <li class="nav-click <#if pageName == "pointproduct">active</#if>" ><a href="/pointproduct"><i class="fa fa-circle-o text-yellow"></i><span>商品配置</span></a></li>
                <li class="nav-click <#if pageName == "pointorder">active</#if>" ><a href="/pointorder"><i class="fa fa-circle-o text-yellow"></i><span>兑换明细</span></a></li>
                <li class="nav-click <#if pageName == "pointquotation">active</#if>" ><a href="/pointquotation"><i class="fa fa-circle-o text-yellow"></i><span>语录配置</span></a></li>
                <li class="nav-click <#if pageName == "pointannounce">active</#if>" ><a href="/pointannounce"><i class="fa fa-circle-o text-yellow"></i><span>消息通知</span></a></li>
                <li class="nav-click <#if pageName == "pointfeedback">active</#if>" ><a href="/pointfeedback"><i class="fa fa-circle-o text-yellow"></i><span>意见反馈</span></a></li>
                <li class="nav-click <#if pageName == "pointquestion">active</#if>" ><a href="/pointquestion"><i class="fa fa-circle-o text-yellow"></i><span>每日一答</span></a></li>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>
</#macro>

<#macro commonFooter >
    <footer class="main-footer">
        Powered by <b>积分后台</b> 1.0.0
        <div class="pull-right hidden-xs">
            <strong>Copyright &copy; 2021-${.now?string('yyyy')}
            </strong><!-- All rights reserved. -->
        </div>
    </footer>
</#macro>