<!DOCTYPE html>
<html>
<head>
    <title>Crawler</title>
</head>
<body>

<form class="crawl form-stacked" action="" method="POST">

    $input(name="update", label="Update", value=form.update, placeholder="Name")$
    $input(name="from", label="From", value=form.from, placeholder="http://www.example.com/feed/xml")$

    $recordDefinition(title="Fields", prefix="", record=form.record)$

    <div class="actions">
        <input type="submit" value="Crawl" name="action" class="crawl"/>
        <input type="submit" value="Save" name="action" class="save"/>
    </div>
</form>

<div class="recordDefinitionTemplate">
    $recordDefinition(title="Fields", prefix="RECORD_PREFIX_REPLACE_ME")$
</div>
</body>
</html>