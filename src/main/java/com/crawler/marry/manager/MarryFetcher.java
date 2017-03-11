package com.crawler.marry.manager;

import com.alibaba.fastjson.JSON;
import com.crawler.marry.model.CrawlerTask;
import com.crawler.marry.parser.*;
import com.crawler.marry.parser.factory.ParserFactory;
import com.crawler.marry.storm.WedStorm;
import com.crawler.marry.util.MarryContact;
import com.crawler.marry.util.ThreadUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by finup on 2017/2/17.
 */
@Component
public class MarryFetcher {

    @Resource
    private WedStorm wedStorm;

    public String fetcher(String type) throws InterruptedException {
       List<MarryFetcherCallable> str  = new ArrayList<>();
        if (StringUtils.isBlank(type)) {
            return "";
        }
        String [] tasks = type.split(",");
        for (String task : tasks) {
            str.add(new MarryFetcherCallable(task));
        }
        List<Future<String>> futures = ThreadUtils.executorService.invokeAll(str);
        ThreadUtils.executorService.submit(new FinishedRunnable(futures));
        return wedStorm.insert();
    }


    public final class MarryFetcherCallable implements Callable<String> {
        private String crawlerTask;

        private MarryFetcherCallable(String task) {
            crawlerTask = task;
        }

        @Override
        public String call() throws Exception {
//            String type = crawlerTask.poll().toString();
            System.out.println(crawlerTask);
            String result = "1";
            switch (crawlerTask) {
                case "1":
                    ParserFactory.createParser(DianPingParser.class).accessNext(MarryContact.DIANPING_ST, MarryContact.DIANPING_HOST);
                    break;
                case "2":
                    ParserFactory.createParser(JieHunParser.class).accessNext(MarryContact.JIEHUN_ST, MarryContact.JIEHUN_HOST);
                    break;
                case "3":
                    ParserFactory.createParser(MeiTuanParser.class).accessNext(MarryContact.MEITUAN_ST, MarryContact.MEITUAN_HOST);
                    break;
                case "4":
                    ParserFactory.createParser(Wed114Parser.class).accessNext(MarryContact.WED_ST, MarryContact.WED_HOST);
                    break;
                 default:
                    result = "2";
                    break;
            }

            return result;
        }
    }

    private final class FinishedRunnable implements Runnable{
        private List<Future<String>> list;

        public FinishedRunnable(List<Future<String>> finishedList ) {
            this.list = finishedList;
        }

        @Override
        public void run() {

            for (Future future : list) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            ThreadUtils.STOP = "2";

        }
    }
    public static void main(String[] args) throws Exception {
        String type="1,2,3,4";
        LinkedBlockingQueue queue = new LinkedBlockingQueue();

        String [] tasks = type.split(",");
        List<MarryFetcherCallable> list = new ArrayList<>();
        for (String task : tasks) {
            queue.put(task);
        }
    }

}
