package com.crawler.marry.parser;

import com.crawler.marry.model.MarryInfo;
import org.apache.http.client.methods.HttpGet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            parserLi(e);

        }
    }

    private MarryInfo parserLi(Element e){
        MarryInfo marryInfo = new MarryInfo();
        Elements elements = e.children();
        if(elements.size() == 2) {
            marryInfo.setLevel(e.getElementsByClass("comment").get(0).text());
            marryInfo.setComment(e.getElementsByClass("count").text().replace("已有","").replace("人点评",""));
            marryInfo.setName(e.getElementsByClass("storename").get(0).text());
            if(e.getElementsByClass("average_price").size() > 0){
                marryInfo.setPrice(e.getElementsByClass("average_price").text().replace("￥", ""));
            }
        }
        return marryInfo;
    }
}
