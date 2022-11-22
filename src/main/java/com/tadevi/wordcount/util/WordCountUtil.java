package com.tadevi.wordcount.util;

import java.util.Map;

public class WordCountUtil {
    private static String[] extractWords(String content) {
        return content.split("\\W+");
    }

    private static void countOneWord(Map<String, Long> counter, String word) {
        if (word.length() == 0) return;

        Long count = counter.get(word);
        if (count == null) {
            counter.put(word, 1L);
        } else {
            counter.put(word, count + 1);
        }
    }

    public static void count(Map<String, Long> counter, String content) {
        String[] words = extractWords(content);
        for (String word : words) {
            countOneWord(counter, word);
        }
    }

    public static void mergeCounter(Map<String, Long> acc, Map<String, Long> counter) {
        for (String key : counter.keySet()) {
            acc.merge(key, counter.get(key), Long::sum);
        }
    }
}
