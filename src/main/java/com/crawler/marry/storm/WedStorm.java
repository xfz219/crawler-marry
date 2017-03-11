package com.crawler.marry.storm;

import com.alibaba.fastjson.JSON;
import com.crawler.marry.model.Comments;
import com.crawler.marry.model.MarryInfo;
import com.crawler.marry.model.TradeMark;
import com.crawler.marry.parser.DianPingParser;
import com.crawler.marry.util.ThreadUtils;
import org.apache.commons.collections.functors.ExceptionClosure;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by finup on 2017/2/20.
 */
@Repository
public class WedStorm {
    private static final Logger LOG = LoggerFactory.getLogger(WedStorm.class);
    private static final long WAIT_TIME = 3000;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String insert(){

        try {
            System.out.println("≈≈≈≈≈≈≈≈≈    start pull data ≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈");

            while (true) {
//                Thread.sleep(WAIT_TIME);
//                System.out.println(1111);
                // 每10个一组  进行分批插入

                if (!ThreadUtils.queue_trademark.isEmpty()) {
                    List<TradeMark> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue_trademark.poll());
                    }
//                    createFile("tradeMark",JSON.toJSONString(list));
                    System.out.println("图片信息 :" + JSON.toJSONString(list));
                    LOG.error(JSON.toJSONString(list));

                }


                if (!ThreadUtils.queue.isEmpty()) {
                    List<MarryInfo> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue.poll());
                    }

//                    createFile("marry",JSON.toJSONString(list));
                    System.out.println("基本信息 :" +  JSON.toJSONString(list));
                    LOG.error(JSON.toJSONString(list));
                }

                if (!ThreadUtils.queue_comment.isEmpty()) {
                    List<Comments> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue_comment.poll());
                    }
//                    createFile("comment",JSON.toJSONString(list));
                    System.out.println("评论 :" + JSON.toJSONString(list));
                    LOG.error(JSON.toJSONString(list));

                }
            }

//            System.out.println(jdbcTemplate.getDataSource().getConnection().getClientInfo().size());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void createFile(String type,String result){
        try {
            String file =  UUID.randomUUID() + ".txt";
            File f = new File("E:\\data" + File.separatorChar + type ,file);
            if (!f.exists()){
              f.createNewFile();
            }
            FileUtils.writeStringToFile(f,result);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new WedStorm().createFile("123","1231313");
        new WedStorm().createFile("123","1211131313");
    }
}
