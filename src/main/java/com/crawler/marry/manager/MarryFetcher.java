package com.crawler.marry.manager;

import com.crawler.marry.model.CrawlerTask;
import com.crawler.marry.storm.WedStorm;
import com.crawler.marry.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

/**
 * Created by finup on 2017/2/17.
 */
@Component
public class MarryFetcher {

    @Resource
    private WedStorm wedStorm;


    public void insert(){
        wedStorm.insert();
    }




    public void fetcher(List<CrawlerTask> tasks) throws InterruptedException {
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
        if (tasks.isEmpty()) {
            return;
        }
        List<Callable<String>> list = new ArrayList<>();
        for (CrawlerTask task : tasks) {
            queue.add(task);
            list.add(new MarryFetcherCallable(task));
        }
        List<Future<String>> futures = ThreadUtils.executorService.invokeAll(list);
    }


    private void crawler() {

    }

    private void perpare(List<CrawlerTask> tasks) {

    }


    public final class MarryFetcherCallable implements Callable<String> {
        private CrawlerTask crawlerTask;

        private MarryFetcherCallable(CrawlerTask task) {
            crawlerTask = task;
        }

        @Override
        public String call() throws Exception {


            System.out.println(new Random().nextInt());
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MarryFetcher marryFetcher = new MarryFetcher();
        List<CrawlerTask> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(new CrawlerTask());
        }

        marryFetcher.fetcher(list);
    }




}
