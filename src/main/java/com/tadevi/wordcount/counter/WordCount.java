package com.tadevi.wordcount.counter;

import java.util.Map;

public interface WordCount {
    Map<String, Long> count() throws Exception;
}
