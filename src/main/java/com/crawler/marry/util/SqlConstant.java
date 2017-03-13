package com.crawler.marry.util;

/**
 * Created by finup on 2017/3/13.
 */
public class SqlConstant {

    public static final String marry_sql = "insert into marryinfo(name,price,comment,summary,scope,type,level,hot,marryId) values(?,?,?,?,?,?,?,?,?)";
    public static final String tradeMark_sql ="insert into trademark(img,commonId,marryId) values(?,?,?)";
    public static final String comment_sql ="insert into comments(content,commonId,rank,marryId) values(?,?,?,?)";
}
