package com.crawler.marry.storm;

import com.alibaba.fastjson.JSON;
import com.crawler.marry.model.Comments;
import com.crawler.marry.model.MarryInfo;
import com.crawler.marry.model.TradeMark;
import com.crawler.marry.parser.DianPingParser;
import com.crawler.marry.util.SqlConstant;
import com.crawler.marry.util.ThreadUtils;
import org.apache.commons.collections.functors.ExceptionClosure;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
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

    public String query(){
        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from crawler_credit_card_basic_info");
        System.out.println("查询出来的list size ：" + list.size());


        return "";
    }

    public String insert() {

        try {
            System.out.println("≈≈≈≈≈≈≈≈≈    start pull data ≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈");

            while (true) {

//                !ThreadUtils.queue_trademark.isEmpty()
//                        && !ThreadUtils.queue.isEmpty()
//                        && !ThreadUtils.queue_comment.isEmpty()
                Thread.sleep(WAIT_TIME);
                System.out.println(1111);
                // 每10个一组  进行分批插入

                if (!ThreadUtils.queue_trademark.isEmpty()) {
                    List<TradeMark> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue_trademark.poll());
                    }
                    try {
                        jdbcTemplate.batchUpdate(SqlConstant.tradeMark_sql, new SaveTradeMark(list));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    jdbcTemplate.batchUpdate(SqlConstant.tradeMark_sql, new SaveTradeMark(list));
                    System.out.println("图片信息 :" + JSON.toJSONString(list));

                }


                if (!ThreadUtils.queue.isEmpty()) {
                    List<MarryInfo> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue.poll());
                    }
                    try {

                        jdbcTemplate.batchUpdate(SqlConstant.marry_sql, new SaveMarry(list));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("基本信息 :" + JSON.toJSONString(list));
                }

                if (!ThreadUtils.queue_comment.isEmpty()) {
                    List<Comments> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        list.add(ThreadUtils.queue_comment.poll());
                    }
                    try {

                        jdbcTemplate.batchUpdate(SqlConstant.comment_sql, new SaveComment(list));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("评论 :" + JSON.toJSONString(list));

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private class SaveMarry implements BatchPreparedStatementSetter {

        private List<MarryInfo> marryInfos;

        private SaveMarry(final List<MarryInfo> list) {
            this.marryInfos = list;
        }

        @Override
        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {

            if (marryInfos.size() > 9) {
                MarryInfo marryInfo = marryInfos.get(i);
                preparedStatement.setString(1, marryInfo.getName());
                preparedStatement.setString(2, marryInfo.getPrice());
                preparedStatement.setString(3, marryInfo.getComment());
                preparedStatement.setString(4, marryInfo.getSummary());
                preparedStatement.setString(5, marryInfo.getScope());
                preparedStatement.setString(6, marryInfo.getType());
                preparedStatement.setString(7, marryInfo.getLevel());
                preparedStatement.setString(8, marryInfo.getHot());
                preparedStatement.setString(9, marryInfo.getMarryId());
            }
        }

        @Override
        public int getBatchSize() {
            return 10;
        }
    }


    private class SaveComment implements BatchPreparedStatementSetter {
        private List<Comments> comments;

        private SaveComment(List<Comments> list) {
            comments = list;
        }

        @Override
        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
            if (comments.size() > 9) {
                Comments c = comments.get(i);
                preparedStatement.setString(1, c.getContent());
                preparedStatement.setString(2, c.getCommonId());
                preparedStatement.setString(3, c.getRank());
                preparedStatement.setString(4, c.getMarryId());
            }


        }

        @Override
        public int getBatchSize() {
            return 10;
        }
    }

    private class SaveTradeMark implements BatchPreparedStatementSetter {
        private List<TradeMark> tradeMarks;

        private SaveTradeMark(List<TradeMark> list) {
            tradeMarks = list;
        }

        @Override
        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
            if (tradeMarks.size() > 9) {
                TradeMark tm = tradeMarks.get(i);
                preparedStatement.setString(1, tm.getImg()==null?"":tm.getImg());
                preparedStatement.setString(2, tm.getCommonId());
                preparedStatement.setString(3, tm.getMarryId());
            }
        }

        @Override
        public int getBatchSize() {
            return 10;
        }
    }

    public void createFile(String type, String result) {
        try {
            String file = UUID.randomUUID() + ".txt";
            File f = new File("E:\\data" + File.separatorChar + type, file);
            if (!f.exists()) {
                f.createNewFile();
            }
            FileUtils.writeStringToFile(f, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new WedStorm().createFile("123", "1231313");
        new WedStorm().createFile("123", "1211131313");
    }
}
