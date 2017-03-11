package com.crawler.marry.storm;

import com.alibaba.fastjson.JSON;
import com.crawler.marry.model.Comments;
import com.crawler.marry.model.MarryInfo;
import com.crawler.marry.model.TradeMark;
import com.crawler.marry.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
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
//                Thread.sleep(WAIT_TIME);
                System.out.println(1111);
                if (!ThreadUtils.queue_trademark.isEmpty()&& ThreadUtils.queue_trademark.size() == 10) {
                    List<TradeMark> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue_trademark.poll());
                    }
                    System.out.println("图片信息 :" + JSON.toJSONString(list));

                }


                if (!ThreadUtils.queue.isEmpty() && ThreadUtils.queue.size() == 10) {
                    List<MarryInfo> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue.poll());
                    }

                    System.out.println("基本信息 :" +  JSON.toJSONString(list));
                }

                if (!ThreadUtils.queue_comment.isEmpty() && ThreadUtils.queue.size() == 10) {
                    List<Comments> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue_comment.poll());
                    }
                    System.out.println("评论 :" + JSON.toJSONString(list));

                }
            }

//            System.out.println(jdbcTemplate.getDataSource().getConnection().getClientInfo().size());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue();
        queue.put("1");
        queue.put("2");
        queue.put("3");
        queue.put("4");
        queue.put("5");
        queue.put("6");
        queue.put("7");
        queue.put("8");
        System.out.println(queue.size());
        for (int i = 0; i < 9; i++) {
            queue.poll();
        }
        System.out.println(queue.size());

    }
}
