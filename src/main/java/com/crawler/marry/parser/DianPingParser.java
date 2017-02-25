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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by finup on 2017/2/20.
 */
public class DianPingParser extends Parser {
    private static final Logger LOG  = LoggerFactory.getLogger(DianPingParser.class);
//    private static final String reg = ".*?target=\\\"_blank\\\">(.*?)封点评.*?";
    private static final String reg = ".*?>([^>]*?)封点评.*?";


    public DianPingParser() {
        super();
    }

    @Override
    public String parserNext(String result) {
        return Jsoup.parse(result).getElementsByClass("NextPage").get(0).attr("href");
    }

    @Override
    public void parser(String result){
        Document document = Jsoup.parse(result);
        Element ul_element = document.getElementsByClass("shop-list").get(0);
        Elements lis_element = ul_element.getElementsByTag("li");
        for (Element li : lis_element) {
            try {
                if (!li.html().contains("top")){
                    if(parserLi(li) != null) {
                        ThreadUtils.queue_dianping.put(parserLi(li));
                    }
                }
            }catch (Exception e) {
                LOG.error("dian ping parser error : " + e.getMessage(),e);
            }
        }
        System.out.println("dianping data: " + JSON.toJSONString(ThreadUtils.queue_dianping));
    }

    private MarryInfo parserLi(Element element) {
        MarryInfo marryInfo = null;
        Elements divs = element.children();
        if (divs.size() == 3) {
            marryInfo = new MarryInfo();
            marryInfo.setName(element.getElementsByClass("shopname").get(0).text());//标题
            marryInfo.setLevel(element.getElementsByClass("irr-star50").attr("title"));//级别
            marryInfo.setScope(element.getElementsByClass("area-list").text());// 区域
            marryInfo.setPrice(element.getElementsByClass("price").text());//价格

            String html = element.html();
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(html);
            if(matcher.find()) {
                marryInfo.setComment(matcher.group(1));
            }

        }
        return marryInfo;
    }

    public static void main(String[] args) throws IOException {
//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        String url = "http://www.dianping.com/search/category/2/55/g163";
//        HttpGet get = new HttpGet(url);
//        CloseableHttpResponse resp = client.execute(get);
//        String result = EntityUtils.toString(resp.getEntity());
//        DianPingParser pingParser = new DianPingParser();
//        String link = pingParser.parserNext(result);
//        pingParser.accessNext(HOST+link);

        ThreadUtils.queue_dianping.offer("123");
        ThreadUtils.queue_dianping.offer("232323");

        System.out.println(JSON.toJSONString(ThreadUtils.queue_dianping));
        ParserFactory.createParser(DianPingParser.class).accessNext(MarryContact.DIANPING_ST,MarryContact.DIANPING_HOST);


    }
}
