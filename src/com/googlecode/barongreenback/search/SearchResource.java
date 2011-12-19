package com.googlecode.barongreenback.search;

import com.googlecode.barongreenback.search.pager.Pager;
import com.googlecode.barongreenback.search.parser.PredicateParser;
import com.googlecode.barongreenback.search.sorter.Sorter;
import com.googlecode.barongreenback.shared.AdvancedMode;
import com.googlecode.barongreenback.shared.ModelRepository;
import com.googlecode.barongreenback.views.Views;
import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Strings;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.records.Keyword;
import com.googlecode.totallylazy.records.Keywords;
import com.googlecode.totallylazy.records.Record;
import com.googlecode.totallylazy.records.Records;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Redirector;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.PathParam;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.annotations.QueryParam;
import org.apache.lucene.queryParser.ParseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.googlecode.barongreenback.shared.RecordDefinition.toKeywords;
import static com.googlecode.barongreenback.views.Views.find;
import static com.googlecode.barongreenback.views.Views.unwrap;
import static com.googlecode.funclate.Model.model;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.notNullValue;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.totallylazy.records.Keywords.keywords;


@Produces(MediaType.TEXT_HTML)
@Path("{view}/search")
public class SearchResource {
    private final Redirector redirector;
    private final AdvancedMode mode;
    private Pager pager;
    private Sorter sorter;
    private final RecordsService recordsService;

    public SearchResource(final Redirector redirector, final AdvancedMode mode,
                          final Pager pager, final Sorter sorter, final RecordsService recordsService) {
        this.redirector = redirector;
        this.mode = mode;
        this.pager = pager;
        this.sorter = sorter;
        this.recordsService = recordsService;
    }

    @POST
    @Path("delete")
    public Response delete(@PathParam("view") String viewName, @QueryParam("query") String query) throws ParseException {
        if (!mode.equals(AdvancedMode.Enable)) {
            return redirector.seeOther(method(on(SearchResource.class).list(viewName, query)));
        }

        recordsService.delete(viewName, query);
        return redirector.seeOther(method(on(SearchResource.class).list(viewName, query)));
    }

    @GET
    @Path("list")
    public Model list(@PathParam("view") final String viewName, @QueryParam("query") final String query) throws ParseException {
        final Either<String, Sequence<Record>> errorOrResults = recordsService.findAll(viewName, query);

        return errorOrResults.map(handleError(viewName, query), listResults(viewName, query));
    }

    @GET
    @Path("unique")
    public Model unique(@PathParam("view") String viewName, @QueryParam("query") String query) throws ParseException {
        final Record record = recordsService.findUnique(viewName, query);

        Map<String, Map<String, Object>> group = record.fields().fold(new LinkedHashMap<String, Map<String, Object>>(), groupBy(Views.GROUP));
        return baseModel(viewName, query).add("record", group);
    }

    private Model baseModel(String viewName, String query) {
        return model().add("view", viewName).add("query", query);
    }

    private Callable1<String, Model> handleError(final String viewName, final String query) {
        return new Callable1<String, Model>() {
            @Override
            public Model call(String errorMessage) throws Exception {
                return baseModel(viewName, query).add("queryException", errorMessage);
            }
        };
    }

    private Callable1<Sequence<Record>, Model> listResults(final String viewName, final String query) {
        return new Callable1<Sequence<Record>, Model>() {
            @Override
            public Model call(Sequence<Record> results) throws Exception {
                if (results.isEmpty()) return baseModel(viewName, query);

                final Sequence<Keyword> visibleHeaders = recordsService.visibleHeaders(viewName);
                return baseModel(viewName, query).
                        add("headers", headers(visibleHeaders, results)).
                        add("pager", pager).
                        add("sorter", sorter).
                        add("sortLinks", sorter.sortLinks(visibleHeaders)).
                        add("sortedHeaders", sorter.sortedHeaders(visibleHeaders)).
                        add("results", pager.paginate(sorter.sort(results, headers(recordsService.view(viewName)))).map(asModel(viewName, visibleHeaders)).toList());
            }
        };
    }

    private Callable1<? super Record, Model> asModel(final String viewName, final Sequence<Keyword> visibleHeaders) {
        return new Callable1<Record, Model>() {
            public Model call(Record record) throws Exception {
                Sequence<Keyword> headers = visibleHeaders.isEmpty() ? record.keywords() : visibleHeaders;
                Model model = model();
                for (Keyword header : headers) {
                    Model field = model().
                            add("value", record.get(header));

                    if (Boolean.TRUE.equals(header.metadata().get(Keywords.UNIQUE))) {
                        field.add("url", uniqueUrlOf(record, header, viewName));
                    }

                    model.add(header.name(), field);
                }
                return model;
            }
        };
    }

    private Uri uniqueUrlOf(Record record, Keyword visibleHeader, String viewName) throws ParseException {
        return redirector.uriOf(method(on(SearchResource.class).
                unique(viewName, String.format("%s:\"%s\"", visibleHeader.name(), record.get(visibleHeader))))).
                dropScheme().dropAuthority();
    }

    public static Callable2<Map<String, Map<String, Object>>, Pair<Keyword, Object>, Map<String, Map<String, Object>>> groupBy(final Keyword<String> lookupKeyword) {
        return new Callable2<Map<String, Map<String, Object>>, Pair<Keyword, Object>, Map<String, Map<String, Object>>>() {
            public Map<String, Map<String, Object>> call(Map<String, Map<String, Object>> map, Pair<Keyword, Object> pair) throws Exception {
                Keyword keyword = pair.first();
                Object value = pair.second();
                String key = keyword.metadata().get(lookupKeyword);
                if (Strings.isEmpty(key)) key = "Other";
                if (!map.containsKey(key)) {
                    map.put(key, new LinkedHashMap<String, Object>());
                }
                map.get(key).put(keyword.name(), value);
                return map;
            }
        };
    }

    private Sequence<Keyword> headers(Model view) {
        return toKeywords(unwrap(view));
    }

    private List<Map<String, Object>> headers(Sequence<Keyword> headers, Sequence<Record> results) {
        if (headers.isEmpty()) {
            return toModel(keywords(results).realise());
        }
        return toModel(headers);
    }

    private List<Map<String, Object>> toModel(Sequence<Keyword> keywords) {
        return keywords.map(asHeader()).
                map(Model.asMap()).
                toList();
    }

    private Callable1<? super Keyword, Model> asHeader() {
        return new Callable1<Keyword, Model>() {
            public Model call(Keyword keyword) throws Exception {
                return model().
                        add("name", keyword.name()).
                        add("escapedName", escape(keyword.name())).
                        add("unique", keyword.metadata().get(Keywords.UNIQUE));
            }
        };
    }

    private String escape(String name) {
        return name.replace(' ', '_');
    }

}
