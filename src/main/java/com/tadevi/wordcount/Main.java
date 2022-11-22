package com.tadevi.wordcount;

import com.tadevi.wordcount.counter.ProducerConsumerWordCount;
import com.tadevi.wordcount.counter.SingleThreadWordCount;
import com.tadevi.wordcount.counter.WordCount;
import com.tadevi.wordcount.log.Logger;
import com.tadevi.wordcount.parser.PageXmlParser;

public class Main {
    private static WordCount getWordCount(PageXmlParser parser, int mode) {
        if (mode == 0) {
            return new SingleThreadWordCount(parser);
        }
        return new ProducerConsumerWordCount(parser, getQueueCapacity(parser.getTotalPages()), getConsumers());
    }

    private static int getQueueCapacity(int pages) {
        return pages / 5;
    }

    private static int getConsumers() {
        int cpu = Runtime.getRuntime().availableProcessors();
        return Math.min(3, cpu / 3);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Use syntax: wordcount <max-pages> <path-to-wiki.xml> <mode>");
            System.out.println("mode 0: single thread counter");
            System.out.println("mode 1: multi-threading counter");
            System.exit(0);
        }
        // arguments
        final int pages = Integer.parseInt(args[0]);
        final String path = args[1];
        final int mode = Integer.parseInt(args[2]);
        Logger.DEBUG = true; // enable log
        final PageXmlParser parser = new PageXmlParser(pages, path);
        final WordCount wordCount = getWordCount(parser, mode);
        Logger.LOG_EXECUTION_TIME("Total time", wordCount::count);
    }
}
