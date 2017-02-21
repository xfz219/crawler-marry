package com.crawler.marry;

import com.crawler.marry.parser.Parser;
import com.crawler.marry.parser.factory.ParserFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by finup on 2017/2/19.
 */
public class Wed114 {
    public static void main(String[] args) throws IOException {

        CloseableHttpClient client = HttpClientBuilder.create().build();

        String url = "http://bj.wed114.cn/sheying/";
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse resp = client.execute(get);
        String result = EntityUtils.toString(resp.getEntity());
        Parser parser = ParserFactory.createParser(com.crawler.marry.parser.Wed114Parser.class);
        String nextUrl = parser.parserNext(result);
        result  = parser.accessNext(nextUrl,"");
        System.out.println(result);

    }
}
