package com.googlecode.barongreenback.views;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.html.Html;
import com.googlecode.utterlyidle.html.Input;

import java.util.UUID;

import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.relativeUriOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ViewEditPage {
    private final HttpHandler httpHandler;
    private final Html html;

    public ViewEditPage(HttpHandler httpHandler, UUID id) throws Exception {
        this(httpHandler, get("/" + relativeUriOf(method(on(ViewsResource.class).edit(id)))).build());
    }

    public ViewEditPage(HttpHandler httpHandler, Request request) throws Exception {
        this(httpHandler, httpHandler.handle(request));
    }

    public ViewEditPage(HttpHandler httpHandler, Response response) throws Exception {
        this.httpHandler = httpHandler;
        this.html = Html.html(response);
        assertThat(html.title(), containsString("Edit View"));
    }

    public Input name() {
        return html.input("//input[@name='view.name']");
    }

    public Input records() {
        return html.input("//input[@name='view.records']");
    }

    public Input query() {
        return html.input("//input[@name='view.query']");
    }

    public Input parent() {
        return html.input("//input[@name='view.parent']");
    }

    public static final String fieldName = "//input[@id='view.keywords[%s].name']";

    public Input fieldName(int index) {
        return html.input(String.format(fieldName, index));
    }

    public ViewListPage save() throws Exception {
        Request request = html.form("//form").submit("descendant::input[@value='Save']");
        Response response = httpHandler.handle(request);
        return new ViewListPage(httpHandler, response);
    }
}