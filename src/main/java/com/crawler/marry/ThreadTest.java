package com.crawler.marry;

import com.crawler.marry.model.CrawlerTask;
import com.crawler.marry.util.ThreadUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by finup on 2017/2/25.
 */
public class ThreadTest {


     public void fetcher() throws InterruptedException {
         LinkedBlockingQueue queue = new LinkedBlockingQueue();
         for (int i = 0; i < 10; i++) {
             CrawlerTask task = new CrawlerTask();
             task.setType(i);
             queue.add(new CallTest(task));
         }

         ThreadUtils.executorService.invokeAll(queue);
     }

    private class CallTest implements Callable<String>{
        private CrawlerTask crawlerTask;

        private CallTest(CrawlerTask task) {
            this.crawlerTask = task;
        }

        @Override
        public String call() throws Exception {
            System.out.println(crawlerTask.getType());
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new ThreadTest().fetcher();
    }
}
