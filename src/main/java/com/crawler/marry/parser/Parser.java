package com.crawler.marry.parser;

import com.crawler.marry.model.Comments;
import com.crawler.marry.model.MarryInfo;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    //评论抓取的页数
    public int page = 5 ;

    public MarryInfo marryInfo = null;
    public List<Comments> listc = new ArrayList<Comments>();


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
            url = parserNext(result);
            if (result.contains("下一页") && org.apache.commons.lang3.StringUtils.isNotBlank(url)) {
                url = Host + parserNext(result);
                System.out.println(url);
                Thread.sleep(1000);
                if (url.contains("jiehun")) {
                    Thread.sleep(3000);
                }
                accessNext(url,Host);
            }
        } catch (Exception e) {
            LOG.error("accessNext error ：" + e.getMessage(),e);
        }
        return "1";
    }

    public void process(){

    }

    public void parserComment(String  result) throws InterruptedException {
    }

    public void  accessComment(String url) {

    }

    protected String excu4Str(CloseableHttpResponse resp) throws IOException {
        return EntityUtils.toString(resp.getEntity());
    }

    public void parser(String result) {
    }



}
