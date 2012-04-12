<!DOCTYPE html>
<html>
<head>
    <title>Queues</title>
    <meta http-equiv="refresh" content="30"/>
</head>
<body class="queues">

<table class="shouldExpand results zebra-striped">
    $thead(headers=["Status", "Request", "Response", "Started", "Completed", "Duration (Seconds)" ])$
    <tbody>
$items:item(item=it)$
    </tbody>
</table>

<div class="actions">
$if(anyExists)$
    $post(name="deleteAll", value="Delete All")$
$else$
    $post(name="deleteAll", value="Delete All", disabled="true")$
$endif$
</div>

</body>
</html>