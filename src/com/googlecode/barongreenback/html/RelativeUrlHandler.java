package com.googlecode.barongreenback.html;

import com.googlecode.totallylazy.Uri;
import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;

public class RelativeUrlHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private Uri currentUri;

    public RelativeUrlHandler(HttpHandler httpHandler) {
        this.httpHandler = httpHandler;
    }

    public Response handle(Request request) throws Exception {
        if (currentUri != null) {
            Uri newUri = request.uri();
            if (newUri.toString().equals("")) {
                request.uri(currentUri);
            } else if (newUri.isRelative()) {
                String absolutePath = currentUri.path(newUri.path()).path();
                request.uri(newUri.path(absolutePath));
            }
        }
        currentUri = request.uri();
        return httpHandler.handle(request);
    }
}