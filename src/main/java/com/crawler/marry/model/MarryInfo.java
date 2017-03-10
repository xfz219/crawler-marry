package com.crawler.marry.model;

/**
 * Created by finup on 2017/2/20.
 */
public class MarryInfo {
    private int id;
    private String name ;//名字
    private String price;//价格
    private String comment;//评论
    private String summary;//摘要
    private String scope;//地区
    private String type;//类型
    private String level;//星级
    private String hot;//人气
    private String marryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public String getMarryId() {
        return marryId;
    }

    public void setMarryId(String marryId) {
        this.marryId = marryId;
    }
}
