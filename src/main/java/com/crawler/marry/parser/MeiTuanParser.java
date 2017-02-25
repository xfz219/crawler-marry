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
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by finup on 2017/2/20.
 */
public class MeiTuanParser extends Parser {
    private static final Logger LOG = LoggerFactory.getLogger(MeiTuanParser.class);

    public MeiTuanParser() {
        super();
    }

    @Override
    public String parserNext(String result) {
//        return Jsoup.parse(result).getElementById("yui_3_16_0_1_1487749711936_3226").attr("href");
        return Jsoup.parse(result).getElementsByClass("next").get(0).children().get(0).attr("href");
    }

    @Override
    public void parser(String result) {
        Document document = Jsoup.parse(result);
        Elements divsInfo = document.select(".poi-tile__info");
        Elements divsMoney = document.select(".poi-tile__money");

        Elements textareas = document.select(".J-bigrender-data");
        for (Element textarea : textareas) {
            if (textarea.toString().contains("邮件订阅")) {
                continue;
            }
            String html = HtmlUtils.htmlUnescape(textarea.text());
            document = Jsoup.parse(html);
            divsInfo.add(document.select(".poi-tile__info").get(0));
            divsMoney.add(document.select(".poi-tile__money").get(0));
        }

        for (int i = 0; i < divsInfo.size(); i++) {
            try {
                MarryInfo marryInfo = parserDiv(divsInfo.get(i), divsMoney.get(i));
                if (marryInfo != null){
                    ThreadUtils.queue_meituan.put(marryInfo);
            }
            } catch (Exception e) {
                LOG.error("dian ping parser error : " + e.getMessage(), e);
            }
        }

        System.out.println("meituan data: " + JSON.toJSONString(ThreadUtils.queue_meituan));
    }

    @Override
    public String accessNext(String url, String Host) {
        try {
            HttpGet get = new HttpGet(url);
            get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Win64; x64; Trident/4.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)");
            get.setHeader("Host", "bj.meituan.com");
            get.setHeader("Upgrade-Insecure-Requests", "1");
            get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
            get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
            get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
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

    private MarryInfo parserDiv(Element elementi, Element elementm) {
        MarryInfo marryInfo = new MarryInfo();

        marryInfo.setName(elementi.getElementsByClass("J-mtad-link").get(0).text());//标题
        marryInfo.setPrice(elementm.getElementsByClass("price").text());//价格
        marryInfo.setScope(elementi.getElementsByClass("tag").get(1).text());// 区域
        if (!elementi.toString().contains("count--empty")) {
            marryInfo.setComment(elementi.getElementsByClass("num").text());//评论数
        } else {
            marryInfo.setComment("暂无评价");//评论数
        }
        marryInfo.setMarryId(UUID.randomUUID().toString());

        //  marryInfo.setLevel(elementi.getElementsByClass("irr-star50").attr("title"));//级别

        return marryInfo;
    }



    @Override
    public void parserComment(String result) {

        super.parserComment(result);
    }

    public static void main(String[] args){
//        CloseableHttpClient client = HttpClientBuilder.create().build();
//        String url = "http://bj.meituan.com/category/hunshaphoto?mtt=1.index/floornew.nc.112.izdwd5gk";
//        HttpGet get = new HttpGet(url);
//        get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Win64; x64; Trident/4.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)");
//        get.setHeader("Host", "bj.meituan.com");
//        get.setHeader("Upgrade-Insecure-Requests", "1");
//        get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//        get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
//        get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
//        CloseableHttpResponse resp = client.execute(get);
//        MeiTuanParser meiTuanParser = new MeiTuanParser();
//
//        String result = EntityUtils.toString(resp.getEntity());
//
//        System.out.println(result);
//        meiTuanParser.parser(result);
        ParserFactory.createParser(MeiTuanParser.class).accessNext(MarryContact.MEITUAN_ST,MarryContact.MEITUAN_HOST);

    }
}
