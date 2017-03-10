package com.crawler.marry.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by finup on 2017/2/17.
 */
public class ThreadUtils {
    public  static  final  ExecutorService executorService = Executors.newFixedThreadPool(5);
    public static LinkedBlockingQueue queue_dianping = new LinkedBlockingQueue();
    public static LinkedBlockingQueue queue_wed = new LinkedBlockingQueue();
    public static LinkedBlockingQueue queue_meituan = new LinkedBlockingQueue();
    public static LinkedBlockingQueue queue_jiehun = new LinkedBlockingQueue();


    public static LinkedBlockingQueue queue = new LinkedBlockingQueue();
    public static String STOP = "1";

}
