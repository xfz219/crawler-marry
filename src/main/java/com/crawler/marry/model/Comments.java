package com.crawler.marry.model;

import java.util.List;

/**
 * Created by finup on 2017/2/25.
 */
public class Comments {

    private int id;

    /**
     *  评论内容
     */
    private String content;
    /**
     * 评论id
     */
    private String commonId;
    /**
     *  评论等级
     */
    private String rank;
    /**
     *  和商品对应的id
     */
    private String marryId;

    private List<TradeMark> imgs;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getMarryId() {
        return marryId;
    }

    public void setMarryId(String marryId) {
        this.marryId = marryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommonId() {
        return commonId;
    }

    public void setCommonId(String commonId) {
        this.commonId = commonId;
    }

    public List<TradeMark> getImgs() {
        return imgs;
    }

    public void setImgs(List<TradeMark> imgs) {
        this.imgs = imgs;
    }
}
