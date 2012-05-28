package com.googlecode.barongreenback.crawler;

import com.googlecode.barongreenback.shared.RecordDefinition;
import com.googlecode.barongreenback.views.Views;
import com.googlecode.lazyrecords.Definition;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Keywords;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Sequence;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URI;

import static com.googlecode.barongreenback.shared.RecordDefinition.RECORD_DEFINITION;
import static com.googlecode.lazyrecords.Definition.constructors.definition;
import static com.googlecode.lazyrecords.Keywords.keyword;
import static com.googlecode.lazyrecords.Record.constructors.record;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CompositeCrawlerTest extends CrawlerTests{
    public static final Keyword<Integer> USER_ID = keyword("summary/userId", Integer.class).metadata(record().set(Keywords.UNIQUE, true));
    public static final Keyword<String> FIRST = Keywords.keyword("first", String.class);
    public static final Keyword<String> FIRST_NAME = keyword("summary/firstName", String.class).as(FIRST);
    public static final Definition USER = definition("/user", USER_ID, FIRST_NAME);

    public static final RecordDefinition ENTRY_DEFINITION = new RecordDefinition(USER);

    public static final Keyword<String> ID = keyword("id", String.class).metadata(record().set(Keywords.UNIQUE, true).set(Views.VISIBLE, true));
    public static final Keyword<URI> LINK = keyword("link/@href", URI.class).
            metadata(record().set(Keywords.UNIQUE, true).set(RECORD_DEFINITION, ENTRY_DEFINITION));
    public static final Keyword<String> UPDATED = keyword("updated", String.class).metadata(record().set(CompositeCrawler.CHECKPOINT, true));
    public static final Keyword<String> TITLE = keyword("title", String.class);
    
    public static final Definition ENTRIES = definition("/feed/entry", ID, LINK, UPDATED, TITLE);

    public static final RecordDefinition ATOM_DEFINITION = new RecordDefinition(ENTRIES);

    @Test
    public void shouldGetTheContentsOfAUrlAndExtractContent() throws Exception {
        Sequence<Record> records = crawl();
        Record entry = records.head();

        assertThat(entry.get(ID), is("urn:uuid:c356d2c5-f975-4c4d-8e2a-a698158c6ef1"));
        assertThat(entry.get(USER_ID), is(1234));
        assertThat(entry.get(FIRST), is("Dan"));
    }

    @Test
    public void shouldNotGoPastTheCheckpoint_checkpointValue() throws Exception {
        Sequence<Record> records = crawl("2011-07-19T12:43:25.000Z");
        assertThat(records.size(), Matchers.<Number>is(1));
    }

    public static Sequence<Record> crawl() throws Exception {
        return crawl(null);
    }

    public static Sequence<Record> crawlOnePageOnly() throws Exception {
        return new CompositeCrawler().crawl(atomXml, "", null, ATOM_DEFINITION);
    }

    public static Sequence<Record> crawl(Object checkpoint) throws Exception {
        return new CompositeCrawler().crawl(atomXml, "/feed/link/@href", checkpoint, ATOM_DEFINITION);
    }



}
