package com.crawler.marry.model;

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
     * 评论图片地址
     */
    private String imgUrl;
    /**
     *  评论等级
     */
    private String rank;
    /**
     *  和商品对应的id
     */
    private String marryId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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
}
