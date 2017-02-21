package com.crawler.marry.storm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by finup on 2017/2/20.
 */
@Repository
public class WedStorm {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(){
        try {
            System.out.println(jdbcTemplate.getDataSource().getConnection().getClientInfo().size());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
