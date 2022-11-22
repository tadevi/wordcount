package com.tadevi.wordcount.counter;

import com.tadevi.wordcount.log.Logger;
import com.tadevi.wordcount.model.Page;
import com.tadevi.wordcount.parser.PageXmlParser;
import com.tadevi.wordcount.util.WordCountUtil;

import java.util.*;
import java.util.concurrent.*;

public class ProducerConsumerWordCount implements WordCount {
    private final PageXmlParser parser;
    private final BlockingQueue<Page> blockingQueue;
    private final ExecutorService executor;
    private final int mConsumers;

    public ProducerConsumerWordCount(final PageXmlParser parser, final int capacity, final int consumers) {
        this.parser = parser;
        this.blockingQueue = new ArrayBlockingQueue<>(capacity);
        this.mConsumers = consumers;
        this.executor = Executors.newFixedThreadPool(consumers + 1);
    }

    private void submitProducer() {
        executor.submit(() -> {
            try {
                for (Page page : parser.getLazyPages()) {
                    blockingQueue.put(page);
                }
                for (int i = 0; i < mConsumers; ++i) {
                    blockingQueue.put(Page.POISON_PILL_PAGE);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<Future<Map<String, Long>>> submitConsumers() {
        List<Future<Map<String, Long>>> consumers = new ArrayList<>(mConsumers);

        for (int i = 0; i < mConsumers; ++i) {
            final int consumerId = i;
            consumers.add(
                    executor.submit(() -> {
                        HashMap<String, Long> counter = new LinkedHashMap<>();
                        while (true) {
                            try {
                                if (blockingQueue.isEmpty()) {
                                    Logger.LOG("Consumer-Producer", "Consumer " + consumerId + " waiting...");
                                }
                                Page page = blockingQueue.take();
                                if (page == Page.POISON_PILL_PAGE) break;

                                WordCountUtil.count(counter, page.getContent());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        return counter;
                    })
            );
        }
        return consumers;
    }

    private void shutdownExecutor() {
        executor.shutdown();
        Logger.LOG_FORCE("Consumer-Producer", "Shutdown executor " + executor.isShutdown());
    }

    // todo: can merge consumers' result in parallel
    private Map<String, Long> mergeConsumerResults(List<Future<Map<String, Long>>> consumers) throws ExecutionException, InterruptedException {
        Map<String, Long> acc = new LinkedHashMap<>();

        for (int i = 0; i < mConsumers; ++i) {
            Map<String, Long> counter = consumers.get(i).get();
            Logger.LOG("Consumer-Producer", "Consumer " + i + " length = " + counter.size());
            WordCountUtil.mergeCounter(acc, counter);
        }
        return acc;
    }

    @Override
    public Map<String, Long> count() throws ExecutionException, InterruptedException {
        submitProducer();
        List<Future<Map<String, Long>>> consumers = submitConsumers();
        Map<String, Long> acc = mergeConsumerResults(consumers);
        shutdownExecutor();
        return acc;
    }
}
