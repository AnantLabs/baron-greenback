package com.googlecode.barongreenback.search;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.records.Records;
import com.googlecode.totallylazy.records.lucene.LuceneRecords;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.modules.ApplicationScopedModule;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.utterlyidle.modules.RequestScopedModule;
import com.googlecode.utterlyidle.modules.ResourcesModule;
import com.googlecode.yadic.Container;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;

public class SearchModule implements ResourcesModule, ApplicationScopedModule, RequestScopedModule {
    public Module addResources(Resources resources) throws ParseException {
        resources.add(annotatedClass(SearchResource.class));
        return this;
    }

    public Module addPerApplicationObjects(Container container) {
        container.addInstance(Version.class, Version.LUCENE_33);
        container.addActivator(Directory.class, DirectoryActivator.class);
        container.addActivator(IndexWriter.class, IndexWriterActivator.class);
        container.add(Analyzer.class, KeywordAnalyzer.class);
        return this;
    }

    public Module addPerRequestObjects(Container container) {
        container.add(LuceneRecords.class);
        container.addActivator(Records.class, container.getActivator(LuceneRecords.class));
        container.add(QueryParserActivator.class);
        return this;
    }

    public static Callable1<? super Pair<Request, Response>, String> file() {
        return new Callable1<Pair<Request, Response>, String>() {
            public String call(Pair<Request, Response> pair) throws Exception {
                return pair.first().url().path().file();
            }
        };
    }


}