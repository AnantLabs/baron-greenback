<div class="fieldDefinition">
    $input(id=prefix + "name", name=prefix + "name", class="keywordName", label="Name", value=keyword.name)$
    $input(id=prefix + "group", name=prefix + "group", class="group", label="Group", value=keyword.group)$
    $select(id=prefix + "type", name=prefix + "type", class="type", label="Type", value=keyword.type, values=types)$
    $checkbox(id=prefix + "unique", name=prefix + "unique", class="unique", label="Unique", checked=keyword.unique)$
    $checkbox(id=prefix + "visible", name=prefix + "visible", class="visible", label="Visible", checked=keyword.visible)$
    <div class="closeIcon"></div>
    <div class="moveIcon"></div>
</div>