package com.googlecode.barongreenback.less;

import com.googlecode.totallylazy.Callable1;

import java.io.IOException;

public interface LessCompiler {
    String compile(String less, Callable1<String, String> loader) throws IOException;
}
