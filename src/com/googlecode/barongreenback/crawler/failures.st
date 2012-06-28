<!DOCTYPE html>
<html>
<head>
    <title>Crawler Failures</title>
</head>
<body class="failures">

$message()$
<table class="shouldExpand results zebra-striped">
    $thead(headers=["Uri", "Response", "Actions"])$
    <tbody>
    $failures: {
    <tr>
        <td><a href="$it.uri$">$it.uri$</a></td>
        <td><div class="entity">$it.response$</div></td>
        <td>
            $post(name="retry", value="Retry", url="/crawler/failures/retry", id=it.id)$
            $post(name="ignore", value="Ignore", url="/crawler/failures/ignore", id=it.id)$
        </td>
    </tr>
    }$
    </tbody>
</table>

<div class="actions">
    $if(anyExists)$
        $post(name="retryAll", value="Retry All", url="/crawler/failures/retryAll")$
        $post(name="ignoreAll", value="Ignore All", url="/crawler/failures/ignoreAll")$
    $else$
        $post(name="retryAll", value="Retry All", disabled="true")$
        $post(name="ignoreAll", value="Ignore All", disabled="true")$
    $endif$
</div>

</body>
</html>