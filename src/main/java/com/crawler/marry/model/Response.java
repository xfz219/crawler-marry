package com.crawler.marry.model;

/**
 * Created by finup on 2017/2/17.
 */
public class Response {

    public static final String SUC_CODE = "00000";
    public static final String FILD_CODE = "11111";

    private boolean isSuc;
    private String errorCode;

    public Response(boolean isSuc, String errorCode) {
        this.isSuc = isSuc;
        this.errorCode = errorCode;
    }
}
