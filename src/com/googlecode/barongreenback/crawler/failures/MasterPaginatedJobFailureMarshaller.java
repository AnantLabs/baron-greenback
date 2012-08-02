package com.googlecode.barongreenback.crawler.failures;

import com.googlecode.barongreenback.crawler.CheckpointHandler;
import com.googlecode.barongreenback.crawler.CrawlerRepository;
import com.googlecode.barongreenback.crawler.MasterPaginatedHttpJob;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.mappings.StringMappings;

import static com.googlecode.lazyrecords.Record.constructors.record;

public class MasterPaginatedJobFailureMarshaller extends AbstractFailureMarshaller {
    private final StringMappings mappings;

    public MasterPaginatedJobFailureMarshaller(CrawlerRepository crawlerRepository, CheckpointHandler checkpointHandler, StringMappings mappings) {
        super(crawlerRepository, checkpointHandler);
        this.mappings = mappings;
    }

    @Override
    public Failure unmarshal(Record record) {
        MasterPaginatedHttpJob job = MasterPaginatedHttpJob.masterPaginatedHttpJob(datasource(record), destination(record), lastCheckpointFor(record), moreUri(record), mappings);
        return Failure.failure(job, record.get(FailureRepository.REASON));
    }
}