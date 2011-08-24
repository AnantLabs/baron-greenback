package com.googlecode.barongreenback.views;

import com.googlecode.barongreenback.shared.RecordDefinition;
import com.googlecode.barongreenback.search.SearchResource;
import com.googlecode.funclate.Model;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.records.Keyword;
import com.googlecode.utterlyidle.MediaType;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.annotations.DefaultValue;
import com.googlecode.utterlyidle.annotations.GET;
import com.googlecode.utterlyidle.annotations.POST;
import com.googlecode.utterlyidle.annotations.Path;
import com.googlecode.utterlyidle.annotations.Produces;
import com.googlecode.utterlyidle.annotations.QueryParam;

import javax.print.DocFlavor;

import static com.googlecode.barongreenback.shared.RecordDefinition.toModel;
import static com.googlecode.funclate.Model.model;
import static com.googlecode.utterlyidle.proxy.Resource.redirect;
import static com.googlecode.utterlyidle.proxy.Resource.resource;
import static com.googlecode.utterlyidle.proxy.Resource.urlOf;

@Produces(MediaType.TEXT_HTML)
@Path("views")
public class ViewsResource {
    private final Views views;

    public ViewsResource(Views views) {
        this.views = views;
    }

    @GET
    @Path("menu")
    public Model menu(@QueryParam("current") @DefaultValue("") String current) {
        return model().add("views", views.get().map(asModel(current)).toList());
    }

    @GET
    @Path("list")
    public Model list() {
        return menu("");
    }

    @GET
    @Path("new")
    public Model createForm() {
        return model();
    }

    @POST
    @Path("new")
    public Response create(RecordDefinition recordDefinition) {
        views.put(View.view(recordDefinition.recordName()).withFields(recordDefinition.fields()));
        return redirectToList();
    }

    @GET
    @Path("edit")
    public Model editForm(@QueryParam("id")String name) {
        View view = views.get(name).get();
        return toModel(view.name(), view.fields());
    }

    @POST
    @Path("edit")
    public Response edit(RecordDefinition recordDefinition) {
        views.put(View.view(recordDefinition.recordName()).withFields(recordDefinition.fields()));
        return redirectToList();
    }

    private Response redirectToList() {
        return redirect(resource(getClass()).list());
    }


    private Callable1<? super View, Model> asModel(final String current) {
        return new Callable1<View, Model>() {
            public Model call(View view) throws Exception {
                Keyword keyword = view.name();
                return model().
                        add("current", keyword.name().equalsIgnoreCase(current)).
                        add("name", keyword.name()).
                        add("url", urlOf(resource(SearchResource.class).find(keyword.name(), "")));
            }
        };
    }
}