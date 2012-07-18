package com.googlecode.barongreenback.crawler;

import com.googlecode.lazyrecords.Definition;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;

public class HttpDatasource {
    protected final Uri uri;
    protected final Definition source;

    public HttpDatasource(Uri uri, Definition source) {
        this.uri = uri;
        this.source = source;
    }

    public static HttpDatasource datasource(Uri uri, Definition source) {
        return new HttpDatasource(uri, source);
    }

    public HttpDatasource uri(Uri uri) {
        return new HttpDatasource(uri, source);
    }

    public Uri uri() {
        return uri;
    }

    public Request request() {
        return RequestBuilder.get(uri).build();
    }

    public Definition source() {
        return source;
    }

    public Sequence<Pair<Keyword<?>, Object>> data() {
        return Sequences.empty();
    }

    @Override
    public int hashCode() {
        return uri().hashCode() * source().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof HttpDatasource) && ((HttpDatasource) obj).uri().equals(uri) && ((HttpDatasource) obj).source().equals(source);
    }
}
