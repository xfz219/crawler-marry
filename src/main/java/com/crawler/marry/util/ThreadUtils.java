package com.crawler.marry.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by finup on 2017/2/17.
 */
public class ThreadUtils {
    public  static  final  ExecutorService executorService = Executors.newFixedThreadPool(5);
}
