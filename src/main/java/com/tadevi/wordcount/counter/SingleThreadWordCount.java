package com.tadevi.wordcount.counter;

import com.tadevi.wordcount.model.Page;
import com.tadevi.wordcount.parser.PageXmlParser;
import com.tadevi.wordcount.util.WordCountUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class SingleThreadWordCount implements WordCount {
    private final PageXmlParser parser;

    public SingleThreadWordCount(PageXmlParser parser) {
        this.parser = parser;
    }

    @Override
    public Map<String, Long> count() throws Exception {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Page page : parser.getLazyPages()) {
            WordCountUtil.count(map, page.getContent());
        }
        return map;
    }
}
