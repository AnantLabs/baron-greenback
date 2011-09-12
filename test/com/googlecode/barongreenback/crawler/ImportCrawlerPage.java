package com.googlecode.barongreenback.crawler;

import com.googlecode.barongreenback.html.Html;
import com.googlecode.barongreenback.html.TextArea;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.proxy.Resource.resource;
import static com.googlecode.utterlyidle.proxy.Resource.urlOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ImportCrawlerPage {
    public static final String MODEL = "//textarea[@name='model']";
    private final HttpHandler httpHandler;
    private final Html html;

    public ImportCrawlerPage(HttpHandler httpHandler) throws Exception {
        this(httpHandler, httpHandler.handle(get(urlOf(resource(CrawlerResource.class).importForm())).build()));
    }

    public ImportCrawlerPage(HttpHandler httpHandler, Response response) throws Exception {
        this.httpHandler = httpHandler;
        this.html = Html.html(response);
        assertThat(html.title(), containsString("Import Crawler"));
    }

    public TextArea model() {
        return html.textarea(MODEL);
    }

    public CrawlerListPage importModel() throws Exception {
        Request request = html.form("//form[contains(@class, 'import')]").submit("descendant::input[@type='submit' and @class='import']");
        Response response = httpHandler.handle(request);
        return new CrawlerListPage(httpHandler, response);
    }
}