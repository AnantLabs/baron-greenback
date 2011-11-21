package com.googlecode.barongreenback.crawler;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.records.Keyword;
import com.googlecode.totallylazy.records.Keywords;
import com.googlecode.totallylazy.records.Record;
import org.junit.Test;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Uri.uri;
import static com.googlecode.totallylazy.matchers.NumberMatcher.is;
import static com.googlecode.totallylazy.records.MapRecord.record;
import static org.hamcrest.MatcherAssert.assertThat;

public class UniqueRecordsTest {
    private final Keyword<Integer> ID = Keywords.keyword("ID", Integer.class).metadata(record().set(Keywords.UNIQUE, true));
    private final Keyword<Integer> SOME_OTHER_KEY = Keywords.keyword("SOME_OTHER_KEY", Integer.class).metadata(record().set(Keywords.UNIQUE, true));
    private final Keyword<Uri> URI = Keywords.keyword("URI", Uri.class).metadata(record().set(Keywords.UNIQUE, true));

    @Test
    public void removesRecordsWithSameUniqueField() throws Exception{
        Sequence<Record> records = sequence(record().set(ID, 12), record().set(ID, 12));
        assertThat(records.size(), is(2));
        assertThat(records.filter(new UniqueRecords(ID)).size(), is(1));
    }

    @Test
    public void ignoresDifferentRecords() throws Exception{
        Sequence<Record> records = sequence(record().set(ID, 12), record().set(ID, 23));
        assertThat(records.size(), is(2));
        assertThat(records.filter(new UniqueRecords(ID)).size(), is(2));
    }

    @Test
    public void ifUniqueKeyIsNotPresentAllowAllRecordsThrough() throws Exception{
        Sequence<Record> records = sequence(record().set(ID, 12), record().set(ID, 23));
        assertThat(records.size(), is(2));
        assertThat(records.filter(new UniqueRecords(SOME_OTHER_KEY)).size(), is(2));
    }

    @Test
    public void worksWithMultipleUniqueFields() throws Exception{
        Sequence<Record> records = sequence(record().set(ID, 12).set(URI, uri("http://server/")), record().set(ID, 12).set(URI, uri("http://DIFFERENT/")), record().set(ID, 12).set(URI, uri("http://server/")));
        assertThat(records.size(), is(3));
        assertThat(records.filter(new UniqueRecords(ID, URI)).size(), is(2));
    }
}