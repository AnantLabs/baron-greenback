package com.googlecode.barongreenback.views;

import com.googlecode.barongreenback.persistence.BaronGreenbackRecords;
import com.googlecode.barongreenback.persistence.ModelMapping;
import com.googlecode.barongreenback.persistence.lucene.NameBasedIndexFacetingPolicy;
import com.googlecode.barongreenback.search.DrillDowns;
import com.googlecode.barongreenback.shared.BaronGreenbackApplicationScope;
import com.googlecode.barongreenback.shared.BaronGreenbackRequestScope;
import com.googlecode.barongreenback.shared.ModelRepository;
import com.googlecode.barongreenback.shared.RecordsModelRepository;
import com.googlecode.funclate.Model;
import com.googlecode.lazyrecords.Definition;
import com.googlecode.lazyrecords.FacetedRecords;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.lucene.LuceneFacetedRecords;
import com.googlecode.lazyrecords.lucene.LucenePartitionedIndex;
import com.googlecode.lazyrecords.lucene.LuceneQueryPreprocessor;
import com.googlecode.lazyrecords.lucene.LuceneStorage;
import com.googlecode.lazyrecords.lucene.PartitionedIndex;
import com.googlecode.lazyrecords.lucene.SearcherPool;
import com.googlecode.lazyrecords.lucene.TaxonomyFacetedLuceneStorage;
import com.googlecode.lazyrecords.lucene.mappings.LuceneMappings;
import com.googlecode.lazyrecords.mappings.StringMappings;
import com.googlecode.lazyrecords.memory.MemoryRecords;
import com.googlecode.totallylazy.Either;
import com.googlecode.totallylazy.Functions;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.collections.PersistentMap;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.RequestBuilder;
import com.googlecode.yadic.Container;
import com.googlecode.yadic.SimpleContainer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static com.googlecode.totallylazy.Functions.returns1;
import static com.googlecode.totallylazy.proxy.Call.method;
import static com.googlecode.totallylazy.proxy.Call.on;
import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.relativeUriOf;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

public class FacetedRecordsActivatorTest {

    @Test
    public void shouldNotUseViewNameForPolicy() throws Exception {
        final Request request = RequestBuilder.get(relativeUriOf(method(on(FacetsResource.class).list("news", "", Either.<String, DrillDowns>right(DrillDowns.empty()))))).build();
        final FacetedRecords facetedRecords = activateFacetedRecordsWith(request);

        assertThat(facetedRecords, Matchers.instanceOf(FacetedRecordsActivator.EmptyFacetedRecords.class));
    }

    @Test
    public void shouldUseShardNameFromViewForPolicy() throws Exception {
        final Request request = RequestBuilder.get(relativeUriOf(method(on(FacetsResource.class).list("booze", "", Either.<String, DrillDowns>right(DrillDowns.empty()))))).build();
        final FacetedRecords facetedRecords = activateFacetedRecordsWith(request);

        assertThat(facetedRecords, Matchers.instanceOf(LuceneFacetedRecords.class));

    }

    private FacetedRecords activateFacetedRecordsWith(Request request) throws Exception {
        final String definitionName = "news";
        final String viewName = "booze";
        final NameBasedIndexFacetingPolicy facetingPolicy = new NameBasedIndexFacetingPolicy(Predicates.is(definitionName));

        final Container parentScope = new SimpleContainer().addInstance(LuceneMappings.class, new LuceneMappings()).
                addInstance(NameBasedIndexFacetingPolicy.class, facetingPolicy).
                addInstance(PartitionedIndex.class, LucenePartitionedIndex.partitionedIndex(Functions.<String, Directory>returns1(new RAMDirectory()), Functions.<Directory, SearcherPool, LuceneStorage>returns2(new TaxonomyFacetedLuceneStorage(null, null, null)))).
                addInstance(LuceneQueryPreprocessor.class, null).
                addInstance(Request.class, null);

        final BaronGreenbackApplicationScope baronGreenbackApplicationScope = new BaronGreenbackApplicationScope(parentScope);
        baronGreenbackApplicationScope.value().addInstance(BaronGreenbackApplicationScope.class, baronGreenbackApplicationScope);
        final BaronGreenbackRequestScope baronGreenbackRequestScope = new BaronGreenbackRequestScope(baronGreenbackApplicationScope.value());
        final ModelRepository modelRepository = new RecordsModelRepository(BaronGreenbackRecords.records(new MemoryRecords(new StringMappings().add(Model.class, new ModelMapping()))));
        new ViewsRepository(modelRepository).set(randomUUID(), ViewsRepository.viewModel(Sequences.<Keyword<?>>empty(), viewName, definitionName, "", true, ""));
        return new FacetedRecordsActivator(baronGreenbackApplicationScope, baronGreenbackRequestScope, modelRepository, request).call();
    }

}