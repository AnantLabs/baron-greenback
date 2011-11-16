package com.googlecode.barongreenback.search.parser;

import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.records.Keyword;
import com.googlecode.totallylazy.records.Record;
import com.googlecode.totallylazy.records.lucene.Lucene;
import com.googlecode.totallylazy.records.lucene.mappings.Mappings;
import com.googlecode.totallylazy.time.Dates;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.totallylazy.records.Keywords.keyword;
import static com.googlecode.totallylazy.records.MapRecord.record;
import static com.googlecode.totallylazy.time.Dates.date;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StandardParserTest {
    @Test
    public void supportsImplicitKeywords() throws Exception {
        Keyword<String> name = keyword("name", String.class);
        PredicateParser predicateParser = new StandardParser(name);
        Predicate<Record> predicate = predicateParser.parse("bob");
        assertThat(predicate.matches(record().set(name, "bob")), is(true));
        assertThat(predicate.matches(record().set(name, "dan")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+((name:[bob TO bob]))"));
    }

    @Test
    public void supportsExplicitKeywords() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:bob");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "bob")), is(true));
        assertThat(predicate.matches(record().set(name, "dan")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:[bob TO bob])"));
    }

    @Test
    public void supportsMultipleConditions() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:bob age:12");

        Keyword<String> name = keyword("name", String.class);
        Keyword<String> age = keyword("age", String.class);
        assertThat(predicate.matches(record().set(name, "bob").set(age, "12")), is(true));
        assertThat(predicate.matches(record().set(name, "bob").set(age, "13")), is(false));
        assertThat(predicate.matches(record().set(name, "dan").set(age, "12")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:[bob TO bob]) +(age:[12 TO 12])"));
    }

    @Test
    public void supportsMultipleConditionsSeparatedByManySpaces() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:bob    age:12");

        Keyword<String> name = keyword("name", String.class);
        Keyword<String> age = keyword("age", String.class);
        assertThat(predicate.matches(record().set(name, "bob").set(age, "12")), is(true));
        assertThat(predicate.matches(record().set(name, "bob").set(age, "13")), is(false));
        assertThat(predicate.matches(record().set(name, "dan").set(age, "12")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:[bob TO bob]) +(age:[12 TO 12])"));
    }

    @Test
    public void supportsNegationWithImplicit() throws Exception {
        PredicateParser predicateParser = new StandardParser(keyword("name", String.class));
        Predicate<Record> predicate = predicateParser.parse("-bob age:12");

        Keyword<String> name = keyword("name", String.class);
        Keyword<String> age = keyword("age", String.class);
        assertThat(predicate.matches(record().set(name, "dan").set(age, "12")), is(true));
        assertThat(predicate.matches(record().set(name, "bob").set(age, "12")), is(false));
        assertThat(predicate.matches(record().set(name, "dan").set(age, "13")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(((-name:[bob TO bob]))) +(age:[12 TO 12])"));
    }

    @Test
    public void supportsNegationWithExplicit() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:-bob age:12");

        Keyword<String> name = keyword("name", String.class);
        Keyword<String> age = keyword("age", String.class);
        assertThat(predicate.matches(record().set(name, "dan").set(age, "12")), is(true));
        assertThat(predicate.matches(record().set(name, "bob").set(age, "12")), is(false));
        assertThat(predicate.matches(record().set(name, "dan").set(age, "13")), is(false));
    }

    @Test
    public void supportsOrWithImplicit() throws Exception {
        PredicateParser predicateParser = new StandardParser(keyword("name", String.class));
        Predicate<Record> predicate = predicateParser.parse("dan,bob");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "dan")), is(true));
        assertThat(predicate.matches(record().set(name, "bob")), is(true));
        assertThat(predicate.matches(record().set(name, "mat")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+((name:[dan TO dan] name:[bob TO bob]))"));
    }

    @Test
    public void supportsOrWithExplicit() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:dan,bob");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "dan")), is(true));
        assertThat(predicate.matches(record().set(name, "bob")), is(true));
        assertThat(predicate.matches(record().set(name, "mat")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:[dan TO dan] name:[bob TO bob])"));
    }

    @Test
    public void ignoreWhitespaces() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("  name  :  dan  ,   bob  ");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "dan")), is(true));
        assertThat(predicate.matches(record().set(name, "bob")), is(true));
        assertThat(predicate.matches(record().set(name, "mat")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:[dan TO dan] name:[bob TO bob])"));
    }

    @Test
    public void supportsQuotedValue() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:\"Dan Bod\"");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "Dan Bod")), is(true));
        assertThat(predicate.matches(record().set(name, "Dan")), is(false));
        assertThat(predicate.matches(record().set(name, "Bod")), is(false));
    }

    @Test
    public void supportsStartsWith() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:Dan*");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "Dan Bod")), is(true));
        assertThat(predicate.matches(record().set(name, "Dan")), is(true));
        assertThat(predicate.matches(record().set(name, "Bod")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:Dan*)"));
    }

    @Test
    public void supportsEndsWith() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:*Bod");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "Dan Bod")), is(true));
        assertThat(predicate.matches(record().set(name, "Dan")), is(false));
        assertThat(predicate.matches(record().set(name, "Bod")), is(true));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:*Bod)"));
    }

    @Test
    public void supportsContains() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name:*ell*");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "Hello")), is(true));
        assertThat(predicate.matches(record().set(name, "Helo")), is(false));
        assertThat(predicate.matches(record().set(name, "ell")), is(true));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:*ell*)"));
    }

    @Test
    public void supportsQuotesContainingNonAlphaNumericCharacters() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("id:\"urn:uuid:c356d2c5-f975-4c4d-8e2a-a698158c6ef1\"");

        Keyword<String> id = keyword("id", String.class);
        assertThat(predicate.matches(record().set(id, "urn:uuid:c356d2c5-f975-4c4d-8e2a-a698158c6ef1")), is(true));
        assertThat(predicate.matches(record().set(id, "fail")), is(false));
    }

    @Test
    public void supportsQuotedName() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("\"First Name\":Dan");

        Keyword<String> name = keyword("First Name", String.class);
        assertThat(predicate.matches(record().set(name, "Dan")), is(true));
        assertThat(predicate.matches(record().set(name, "Mat")), is(false));
    }

    @Test
    public void supportsEmptyQueries() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("");

        Keyword<String> name = keyword("First Name", String.class);
        assertThat(predicate.matches(record().set(name, "Dan")), is(true));
        assertThat(predicate.matches(record().set(name, "Mat")), is(true));
    }

    @Test
    public void supportsExplicitDateBasedQueries() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("dob:2001/1/10");

        Keyword<Date> dob = keyword("dob", Date.class);
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 10))), is(true));
        assertThat(predicate.matches(record().set(dob, date(2001, 10, 1))), is(false));
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 10, 3, 15, 59, 123))), is(true));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(dob:[20010110000000000 TO 20010110235959000])"));
    }

    @Test
    public void supportsImplicitDateBasedQueries() throws Exception {
        Keyword<Date> dob = keyword("dob", Date.class);
        PredicateParser predicateParser = new StandardParser(dob);
        Predicate<Record> predicate = predicateParser.parse("2001/1/10");

        assertThat(predicate.matches(record().set(dob, date(2001, 1, 10))), is(true));
        assertThat(predicate.matches(record().set(dob, date(2001, 10, 1))), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+((dob:[20010110000000000 TO 20010110235959000]))"));
    }

    @Test
    public void supportsGreaterThanQueries() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("dob > 2001/1/10");

        Keyword<Date> dob = keyword("dob", Date.class);
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 11))), is(true));
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 10))), is(false));
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 9))), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(dob:{20010110000000000 TO *])"));
    }

    @Test
    public void supportsLowerThanDateQueries() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("dob < 2001/1/10");

        Keyword<Date> dob = keyword("dob", Date.class);
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 9))), is(true));
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 11))), is(false));
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 10))), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(dob:[* TO 20010110000000000})"));
    }

    @Test
    public void supportsLowerThanStringQueries() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name < Dan");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "Bob")), is(true));
        assertThat(predicate.matches(record().set(name, "Dan")), is(false));
        assertThat(predicate.matches(record().set(name, "Mat")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:[* TO Dan})"));
    }
    
    @Test
    public void supportsGreaterThanStringQueries() throws Exception {
        PredicateParser predicateParser = new StandardParser();
        Predicate<Record> predicate = predicateParser.parse("name > Dan");

        Keyword<String> name = keyword("name", String.class);
        assertThat(predicate.matches(record().set(name, "Mat")), is(true));
        assertThat(predicate.matches(record().set(name, "Dan")), is(false));
        assertThat(predicate.matches(record().set(name, "Bob")), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+(name:{Dan TO *])"));
    }

    @Test
    public void supportsImplicitDateBasedComparisonsQueries() throws Exception {
        Keyword<Date> dob = keyword("dob", Date.class);
        PredicateParser predicateParser = new StandardParser(dob);
        Predicate<Record> predicate = predicateParser.parse("> 2001/1/10");

        assertThat(predicate.matches(record().set(dob, date(2001, 1, 11))), is(true));
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 10))), is(false));
        assertThat(predicate.matches(record().set(dob, date(2001, 1, 9))), is(false));

        String luceneQuery = new Lucene(new Mappings()).query(predicate).toString();
        assertThat(luceneQuery, is("+((dob:{20010110000000000 TO *]))"));
    }
}
