package com.crawler.marry.storm;

import com.crawler.marry.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by finup on 2017/2/20.
 */
@Repository
public class WedStorm {

    private static final long WAIT_TIME = 3000;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(){

        try {
            System.out.println("≈≈≈≈≈≈≈≈≈    start pull data ≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈");

            while (true) {
                Thread.sleep(WAIT_TIME);
                System.out.println("≈≈≈≈≈≈≈≈≈   pull data ≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈");
                System.out.println(ThreadUtils.queue_dianping.poll());
                System.out.println(ThreadUtils.queue_jiehun.poll());
                System.out.println(ThreadUtils.queue_wed.poll());
                System.out.println(ThreadUtils.queue_meituan.poll());
            }

//            System.out.println(jdbcTemplate.getDataSource().getConnection().getClientInfo().size());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LinkedBlockingQueue queue = new LinkedBlockingQueue();
        System.out.println(queue.poll());
    }
}
