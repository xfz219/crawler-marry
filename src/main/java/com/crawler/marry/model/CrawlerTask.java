package com.crawler.marry.model;

import java.io.Serializable;

/**
 * Created by finup on 2017/2/17.
 */

public class CrawlerTask implements Serializable {

    private String url;
    private boolean title = true;
    private boolean address = true ;
    private boolean monery = true;
    private boolean commentNum = true;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isTitle() {
        return title;
    }

    public void setTitle(boolean title) {
        this.title = title;
    }

    public boolean isAddress() {
        return address;
    }

    public void setAddress(boolean address) {
        this.address = address;
    }

    public boolean isMonery() {
        return monery;
    }

    public void setMonery(boolean monery) {
        this.monery = monery;
    }

    public boolean isCommentNum() {
        return commentNum;
    }

    public void setCommentNum(boolean commentNum) {
        this.commentNum = commentNum;
    }
}
