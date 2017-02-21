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
 * Created by finup on 2017/2/19.
 */
public class Wed114Parser extends Parser {
    private static final Logger LOG  = LoggerFactory.getLogger(Wed114Parser.class);
    //    private static final String reg = ".*?target=\\\"_blank\\\">(.*?)封点评.*?";
    private static final String HOST = "http://bj.wed114.cn";

    public Wed114Parser() {
        super();
    }

    @Override
    public String parserNext(String result) {
        String nextUrl = "";
        Element e = Jsoup.parse(result).getElementsByClass("pagenav").get(0);
        Elements as = e.getElementsByTag("a");
        for(Element a : as) {
            if (a.html().contains("下一页")) {
                nextUrl = a.attr("href");
            }
        }
        return nextUrl;
    }


    @Override
    public void parser(String result) {
        Document doc = Jsoup.parse(result);
        Element element = doc.getElementsByClass("sheyLbox").get(0);
        Elements divs = element.children();
        for (Element div : divs) {
            parserDiv(div);
        }
    }

    private void parserDiv(Element element) {
        MarryInfo marryInfo = new MarryInfo();
        Elements childrens = element.children();
        if (childrens.size() == 2) {
            marryInfo.setComment(element.getElementsByClass("pinglun").get(0).text().replace("评论",""));
            marryInfo.setHot(element.getElementsByClass("renqi").get(0).text().replace("人气",""));
            marryInfo.setName(element.getElementsByClass("inshoptit").get(0).text());
            marryInfo.setScope(element.getElementsByClass("inaddtxt").get(0).text());
            marryInfo.setPrice(element.getElementsByClass("ismoney").get(0).text().replace("￥","").replace("起",""));

        }
    }

}
