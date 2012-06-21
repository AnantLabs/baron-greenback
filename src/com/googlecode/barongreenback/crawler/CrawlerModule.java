package com.googlecode.barongreenback.crawler;

import com.googlecode.barongreenback.persistence.StringPrintStream;
import com.googlecode.barongreenback.shared.RecordDefinition;
import com.googlecode.barongreenback.shared.RecordDefinitionActivator;
import com.googlecode.utterlyidle.Resources;
import com.googlecode.utterlyidle.modules.*;
import com.googlecode.yadic.Container;

import java.io.PrintStream;
import java.util.concurrent.*;

import static com.googlecode.utterlyidle.annotations.AnnotatedBindings.annotatedClass;

public class CrawlerModule implements ResourcesModule, ArgumentScopedModule, RequestScopedModule, ApplicationScopedModule {
    public Module addResources(Resources resources) throws Exception {
        resources.add(annotatedClass(CrawlerResource.class));
        resources.add(annotatedClass(BatchCrawlerResource.class));
        resources.add(annotatedClass(CrawlerStatusResource.class));
        resources.add(annotatedClass(CrawlerFailureResource.class));
        return this;
    }

    public Module addPerArgumentObjects(Container container) throws Exception {
        container.addActivator(RecordDefinition.class, RecordDefinitionActivator.class);
        return this;
    }

    public Module addPerRequestObjects(Container container) throws Exception {
        container.add(CompositeCrawler.class);
        container.add(CheckPointHandler.class);
        container.add(Crawler.class, QueuesCrawler.class);
        container.add(CrawlInterval.class);
        container.addInstance(PrintStream.class, new StringPrintStream());
        return this;
    }

    @Override
    public Module addPerApplicationObjects(Container container) throws   Exception {
        container.add(CrawlerFailures.class);
        container.addInstance(InputHandler.class, new InputHandler(executor(100, new LinkedBlockingQueue<Runnable>(100))));
        container.addInstance(ProcessHandler.class, new ProcessHandler(executor(100, new LinkedBlockingQueue<Runnable>(50))));
        container.addInstance(OutputHandler.class, new OutputHandler(executor(1, new LinkedBlockingQueue<Runnable>())));
        return this;
    }

    private ThreadPoolExecutor executor(int threads, LinkedBlockingQueue<Runnable> workQueue) {
        return new ThreadPoolExecutor(threads, threads,
                0L, TimeUnit.MILLISECONDS,
                workQueue,
                new BlockingRetryRejectedExecutionHandler());
    }

}