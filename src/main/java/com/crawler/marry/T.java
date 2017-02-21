package com.crawler.marry;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by finup on 2017/2/17.
 */
public class T {

    public static final String HOST = "https://www.dianping.com";


    public static void main(String[] args) throws IOException {

        CloseableHttpClient client = HttpClientBuilder.create().build();

        String url = "http://www.dianping.com/search/category/2/55/g163";
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse resp = client.execute(get);
        String result = EntityUtils.toString(resp.getEntity());
        System.out.println(result);
        parser(result);

        if (!result.contains("下一页")) {

           url = HOST + processNext(result);
           next(url,client);
        }
    }

    public static void next(String url,CloseableHttpClient client ) throws IOException {
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse resp = client.execute(get);
        String result = EntityUtils.toString(resp.getEntity());
        while (result.contains("下一页")) {
            url = HOST + processNext(result);
            next(url,client);
        }

    }

    public static String processNext(String result){
        String url = Jsoup.parse(result).getElementsByClass("NextPage").get(0).attr("href");
        System.out.println("---------------next page : -----------------" + url);
        return url;
    }

    public static void parser(String result){
        Document document = Jsoup.parse(result);
        Element ul_element = document.getElementsByClass("shop-item-list").get(0);
        Elements lis_element = ul_element.getElementsByTag("li");
        for (Element li : lis_element) {
            parserLi(li);
        }
    }

    private static void parserLi(Element element) {
        Elements divs = element.children();
        if (divs.size() == 3) {
            System.out.println("======================================");
            System.out.println(divs.get(1).getElementsByTag("h5").get(0).text());
            System.out.println(divs.get(1).getElementsByTag("h3").get(0).text());
            System.out.println(divs.get(2).getElementsByTag("div").get(0).text());
            System.out.println("======================================");

        }
    }

}
