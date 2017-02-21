package com.crawler.marry.parser;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * Created by finup on 2017/2/20.
 */
public class MeiTuanParser extends Parser{
    private static final Logger LOG  = LoggerFactory.getLogger(MeiTuanParser.class);

    public MeiTuanParser() {
        super();
    }

    @Override
    public String parserNext(String result) {
        return Jsoup.parse(result).getElementsByClass("NextPage").get(0).attr("href");
    }


    public static void main(String[] args) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String url = "http://bj.meituan.com/category/hunshaphoto?mtt=1.index/floornew.nc.112.izdwd5gk";
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse resp = client.execute(get);
        String result = EntityUtils.toString(resp.getEntity());
        System.out.println(result);
    }
}
