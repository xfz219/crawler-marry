package com.crawler.marry.parser;

import com.alibaba.fastjson.JSON;
import com.crawler.marry.model.MarryInfo;
import com.crawler.marry.parser.factory.ParserFactory;
import com.crawler.marry.util.MarryContact;
import com.crawler.marry.util.ThreadUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by finup on 2017/2/20.
 */
public class JieHunParser extends Parser {
    private static final Logger LOG  = LoggerFactory.getLogger(JieHunParser.class);

    public JieHunParser() {
        super();
    }

    @Override
    public String parserNext(String result) {
        return Jsoup.parse(result).getElementsByClass("pager").get(0).
                getElementsByClass("n").get(0).
                getElementsByTag("a").get(0).attr("href");

    }

    @Override
    public void parser(String result) {

        Document document = Jsoup.parse(result);
        Element ul_element = document.getElementsByClass("stlist_con").get(0);
        Elements lis_element = ul_element.getElementsByTag("li");
        for (Element e : lis_element) {
            try {

                MarryInfo marryInfo = parserLi(e);
                if (marryInfo!= null)
                    ThreadUtils.queue_jiehun.put(marryInfo);
            } catch (Exception es) {
                es.printStackTrace();
            }

        }
        System.out.println("jiehun data: " + JSON.toJSONString(ThreadUtils.queue_jiehun));
    }

    private MarryInfo parserLi(Element e){
        MarryInfo marryInfo = null;
        Elements elements = e.children();
        if(elements.size() == 2) {
            marryInfo  = new MarryInfo();
            marryInfo.setLevel(e.getElementsByClass("comment").get(0).text());
            marryInfo.setComment(e.getElementsByClass("count").text().replace("已有","").replace("人点评",""));
            marryInfo.setName(e.getElementsByClass("storename").get(0).text());
            if(e.getElementsByClass("average_price").size() > 0){
                marryInfo.setPrice(e.getElementsByClass("average_price").text().replace("￥", ""));
            }
            marryInfo.setMarryId(UUID.randomUUID().toString());
        }
        return marryInfo;
    }


    @Override
    public void parserComment(String result) {

        super.parserComment(result);
    }



    public static void main(String[] args) throws IOException {

//       String url = "http://bj.jiehun.com.cn/hunshasheying/storelists/";
//
//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        HttpGet get = new HttpGet(url);
//        CloseableHttpResponse resp = client.execute(get);
//        JieHunParser meiTuanParser = new JieHunParser();
//
//        String result = EntityUtils.toString(resp.getEntity());
//
//        System.out.println(result);
//        meiTuanParser.parser(result);

        ParserFactory.createParser(JieHunParser.class).accessNext(MarryContact.JIEHUN_ST,MarryContact.JIEHUN_HOST);

    }
}
