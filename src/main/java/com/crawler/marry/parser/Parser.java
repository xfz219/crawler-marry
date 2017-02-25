package com.crawler.marry.parser;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by finup on 2017/2/19.
 */
public abstract class Parser {
    private static final Logger LOG  = LoggerFactory.getLogger(Parser.class);
    protected CloseableHttpClient client ;
    // 评论url
    public   String comment_url = "";
    // 商标url
    public String trademark_url ="";

    public Parser() {
        System.out.println("parser client");
        client = HttpClientBuilder.create().build();
    }

    public abstract  String parserNext(String result);

    public String accessNext(String url,String Host){
        try {
            HttpGet get = new HttpGet(url);
            String result = excu4Str(client.execute(get));
            this.parser(result);

            if (result.contains("下一页")) {
                url = Host + parserNext(result);
                System.out.println(url);
                Thread.sleep(1000);
                accessNext(url,Host);
            }
        } catch (Exception e) {
            LOG.error("accessNext error ：" + e.getMessage(),e);
        }
        return null;
    }

    public void process(){

    }

    public void parserComment(String result){

    }

    public void  accessComment(String url) {

    }

    protected String excu4Str(CloseableHttpResponse resp) throws IOException {
        return EntityUtils.toString(resp.getEntity());
    }

    public void parser(String result) {
    }



}
