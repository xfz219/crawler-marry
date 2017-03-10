package com.crawler.marry.util;

import com.alibaba.fastjson.JSONObject;
import com.crawler.marry.model.Comments;
import com.crawler.marry.model.TradeMark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Administrator on 2017/3/9.
 */
public class JdbcUtils {

        private static final String jdbcDriver;
        private static final String jdbcUser;
        private static final String jdbcPwd;

        private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

        static {
            ResourceBundle bundle = ResourceBundle.getBundle("config");
            jdbcDriver = bundle.getString("jdbc.driver");
            jdbcUser = bundle.getString("jdbc.user");
            jdbcPwd = bundle.getString("jdbc.pwd");
        }

        public static void save(JSONObject jsonObject) {
            logger.info("开始进行数据处理");
            if (jsonObject == null) {
                logger.warn("jsonObject is null.");
                return;
            }

            Connection conn = createConnection();
            try {
                saveDate(conn, jsonObject);
                saveComments(conn, jsonObject);
            } catch (Exception ex) {
                logger.error("save to mysql error", ex);
            } finally {
                closeConnection(conn);
            }
        }

        private static void saveDate(Connection conn, JSONObject jsonObject) {
            logger.info("进入存储基本信息");
            JSONObject json = jsonObject.getJSONObject("MarryInfo");

            String sql = "insert into marryinfo(name,price,comment,summary,scope,type,level,hot,marryId) values(?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, json.getString("name"));
                ps.setString(2, json.getString("price"));
                ps.setString(3, json.getString("comment"));
                ps.setString(4, json.getString("summary"));
                ps.setString(5, json.getString("scope"));
                ps.setString(6, json.getString("type"));
                ps.setString(7, json.getString("level"));
                ps.setString(8, json.getString("hot"));
                ps.setString(9, json.getString("marryId"));
                boolean str = ps.execute();
                logger.info(json.getString("name")+"入库成功boolean======="+str);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeStatement(ps);
            }
        }


    private static void saveComments(Connection conn, JSONObject jsonObject) {
        List<Comments> listc = (List<Comments>)jsonObject.get("Comments");

        String sql = "insert into comments(content,commonId,rank,marryId) values(?,?,?,?)";
        for (Comments c : listc) {
            PreparedStatement ps = null;
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, c.getContent());
                ps.setString(2, c.getCommonId());
                ps.setString(3, c.getRank());
                ps.setString(4, c.getMarryId());
                ps.execute();

                saveImg(conn,c.getImgs());
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeStatement(ps);
            }
        }
    }

    private static void saveImg(Connection conn,  List<TradeMark> imgs) {
        String sql = "insert into trademark(img,commonId,marryId) values(?,?,?)";
        if(imgs != null){
            for (TradeMark tm : imgs) {
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, tm.getImg());
                    ps.setString(2, tm.getCommonId());
                    ps.setString(3, tm.getMarryId());
                    ps.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    closeStatement(ps);
                }
            }
        }
    }

        public static Connection createConnection() {
            try {
                return DriverManager.getConnection(jdbcDriver, jdbcUser, jdbcPwd);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public static void closeConnection(Connection conn) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        public static void closeStatement(Statement statement) {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        public static String updateToCNBracket(String name){
            String result = null;
            if (name != null){
                result = name.replace("(", "（").replace(")", "）");
            }
            return result;
        }
}
