<!DOCTYPE html>
<html>
<head>
    <title>$title$ - Baron Greenback</title>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1"/>
    <link rel="shortcut icon" href="$base$baron-greenback/shared/favicon.ico"/>
    <link rel="stylesheet" type="text/css" href="$base$baron-greenback/shared/style.less"/>
    <script src="$base$baron-greenback/shared/less-1.3.1.js"></script>
    $head$
</head>
<body class="$body.class$ bgb">

<div class="navbar navbar-inverse navbar-static-top">
    <div class="navbar-inner">
        <div>
            <a href="/" class="home brand pull-left">Baron Greenback: $title$</a>
            <ul class="nav pull-right">
                <li class="dropdown">
                    <ul class="dropdown-menu">
                        $bgbAdmin()$
                    </ul>
                    <a href="#" class="dropdown-toggle">Admin</a>
                </li>
            </ul>
        </div>
    </div>
</div>

<div class="content search-page">
    $navigation()$
    $body$
</div>
<script type="text/javascript" src="$base$baron-greenback/shared/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/jquery-ui-1.10.3.custom.min.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/sugar.min.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/FixedHeader.nightly.min.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/bgb.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/tables.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/subfeed.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/expander/expander.js"></script>
<script type="text/javascript" src="$base$baron-greenback/shared/faceting.js"></script>
</body>
</html>