package com.tadevi.wordcount.log;

import java.util.concurrent.Callable;

public class Logger {
    public static volatile boolean DEBUG = true;

    public static void LOG(String tag, Object data) {
        if (DEBUG) {
            LOG_FORCE(tag, data);
        }
    }

    public static void LOG_FORCE(String tag, Object data) {
        System.err.println("[" + tag + "]: " + data.toString());
    }

    public static <T> T LOG_EXECUTION_TIME(String tag, Callable<T> callable) {
        long elap = System.currentTimeMillis();
        T result = null;
        try {
            result = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        elap = System.currentTimeMillis() - elap;
        Logger.LOG_FORCE(tag, elap * 1f / 1000 + "s");
        return result;
    }
}
