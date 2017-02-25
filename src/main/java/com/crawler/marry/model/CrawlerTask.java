package com.crawler.marry.model;

import java.io.Serializable;

/**
 * Created by finup on 2017/2/17.
 */

public class CrawlerTask implements Serializable {


    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
