package com.googlecode.barongreenback.crawler;

import com.googlecode.barongreenback.shared.ApplicationTests;
import com.googlecode.barongreenback.shared.BaronGreenbackProperties;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Strings;
import com.googlecode.yadic.Container;
import org.junit.Test;

import java.util.UUID;

public class QueuesCrawlerTest extends ApplicationTests {
    @Test(expected = IllegalStateException.class)
    public void shouldBeExactlyOneUniqueField() throws Exception {
        importAndCrawl("manyUniques.json");
    }

    @Test
    public void shouldCrawlOkWhenOnlyOneUniqueField() throws Exception {
        importAndCrawl("oneUnique.json");
    }

    private void importAndCrawl(String filename) throws Exception {
        application.usingRequestScope(get(BaronGreenbackProperties.class)).setProperty(CrawlerActivator.PROPERTY_NAME, QueuesCrawler.class.getName());

        CrawlerImportPage importer = new CrawlerImportPage(browser);
        UUID id = UUID.randomUUID();
        importer.importCrawler(Strings.toString(QueuesCrawlerTest.class.getResourceAsStream(filename)), Option.some(id));

        Crawler crawler = application.usingRequestScope(get(Crawler.class));

        crawler.crawl(id);
    }

    private <T> Callable1<Container, T> get(final Class<T> aClass) {
        return new Callable1<Container, T>() {
            @Override
            public T call(Container container) throws Exception {
                return container.get(aClass);
            }
        };
    }
}