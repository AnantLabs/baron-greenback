package com.googlecode.barongreenback.crawler;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Keywords;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import org.junit.Test;

import static com.googlecode.lazyrecords.Record.constructors.record;
import static com.googlecode.totallylazy.Sequences.one;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CheckPointStopper2Test {
    @Test
    public void stopsOnceCheckPointIsReached() throws Exception {
        Keyword<String> name = Keywords.keyword("name", String.class).metadata(record().set(CompositeCrawler.CHECKPOINT, true));
        Record dan = record().set(name, "Dan");
        Sequence<Record> allRecords = Sequences.sequence(dan, record().set(name, "Raymond"));
        Sequence<Record> result = CheckPointStopper2.stopAt("Raymond", allRecords);
        assertThat(result, is(one(dan)));
    }
}