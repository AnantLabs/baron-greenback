package com.googlecode.barongreenback.search;

import com.googlecode.lazyrecords.parser.ParserParameters;
import com.googlecode.utterlyidle.modules.Module;
import com.googlecode.yadic.Container;

public interface ParserParametersModule extends Module {
    ParserParameters addParameters(ParserParameters parameters, Container container);
}
