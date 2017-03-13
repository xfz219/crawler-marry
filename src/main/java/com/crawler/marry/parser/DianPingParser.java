package com.crawler.marry.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crawler.marry.model.Comments;
import com.crawler.marry.model.MarryInfo;
import com.crawler.marry.model.TradeMark;
import com.crawler.marry.parser.factory.ParserFactory;
import com.crawler.marry.util.JdbcUtils;
import com.crawler.marry.util.MarryContact;
import com.crawler.marry.util.ThreadUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by finup on 2017/2/20.
 */
public class DianPingParser extends Parser {
    private static final Logger LOG = LoggerFactory.getLogger(DianPingParser.class);
    //    private static final String reg = ".*?target=\\\"_blank\\\">(.*?)封点评.*?";
    private static final String reg = ".*?>([^>]*?)封点评.*?";


    String url = "";

    public DianPingParser() {
        super();
    }

    @Override
    public String parserNext(String result) {
        if (Jsoup.parse(result).getElementsByClass("NextPage").isEmpty()) {
            return "";
        }
        return Jsoup.parse(result).getElementsByClass("NextPage").get(0).attr("href");
    }

    @Override
    public void parser(String result) {
        Document document = Jsoup.parse(result);
        Element ul_element = document.getElementsByClass("shop-list").get(0);
        Elements lis_element = ul_element.getElementsByTag("li");

        for (Element li : lis_element) {
            try {
                if (!li.html().contains("top")) {
                    if (parserLi(li) != null) {
                        parserLi(li);
                    }
                }
            } catch (Exception e) {
                LOG.error("dian ping parser error : " + e.getMessage(), e);
            }
        }
    }

    private JSONObject parserLi(Element element) {
        JSONObject json = new JSONObject();
        try {

            Elements divs = element.children();
            if (divs.size() == 3) {
                marryInfo = new MarryInfo();
                marryInfo.setName(element.getElementsByClass("shopname").get(0).text());//标题
                marryInfo.setLevel(element.getElementsByClass("irr-star50").attr("title"));//级别
                marryInfo.setScope(element.getElementsByClass("area-list").text());// 区域
                marryInfo.setPrice(element.getElementsByClass("price").text());//价格
                marryInfo.setMarryId(UUID.randomUUID().toString());

                String html = element.html();
                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(html);
                if (matcher.find()) {
                    marryInfo.setComment(matcher.group(1));
                }
            }
            ThreadUtils.queue.put(marryInfo);
            parserComment(element.toString());
        } catch (Exception e) {

        }
//
        return json;
    }

    @Override
    public void parserComment(String result) throws InterruptedException {
        Document doc = Jsoup.parse(result);
        try {
            Elements as = doc.getElementsByTag("a");
            String url = "";
            for (Element a : as) {
                if (a.text().contains("封点评")) {
                    url = a.attr("href");
                    String shopId = url.split("#")[0].split("/")[2];
                    url = "http://www.dianping.com/ajax/shop/wedding/reviewlist?_nr_force=" + shopId + "&act=getreviewlist&shopid=" + shopId + "&tab=all";
                    break;
                }
            }

            if (StringUtils.isNotBlank(url)) {
                HttpGet get = new HttpGet(url);
                System.out.println("点评url :" + url);
                CloseableHttpResponse resp = client.execute(get);
                result = EntityUtils.toString(resp.getEntity());

                JSONObject jsonObject = JSON.parseObject(result);
                String html = jsonObject.getString("msg");
                result = HtmlUtils.htmlUnescape(html);
                if (result.contains("更多点评")) {
                    doc = Jsoup.parse(html);

                    for (Element a : doc.select("a")) {
                        if (a.text().contains("更多点评")) {
                            url = "http://www.dianping.com" + a.attr("href").split("#")[0];
                        }
                    }

                    get = new HttpGet(url);
                    resp = client.execute(get);
                    result = EntityUtils.toString(resp.getEntity());

                    //解析第一页的评论内容
                    doc = Jsoup.parse(result);
                    Elements divs = doc.getElementsByClass("content");
                    for (Element div : divs) {
                        Comments comments = new Comments();
                        if (div.toString().contains("comment-entry")) {
                            comments.setContent(div.select(".comment-entry").get(0).text());
                        }
                        if (div.toString().contains("comment-rst")) {
                            comments.setRank(div.select(".comment-rst").get(0).getElementsByTag("span").get(0).attr("class").split(" ")[1]);
                        }

                        comments.setMarryId(marryInfo.getMarryId());
                        comments.setCommonId(UUID.randomUUID().toString());
                        ThreadUtils.queue_comment.put(comments);
                        if (div.toString().contains("shop-info-gallery")) {
                            parserImg(comments, div.getElementsByClass("shop-info-gallery").get(0));
                        }

                    }
                    //解析剩余内容的评论
                    parserNeatPage(result);

                } else {

                    Elements lis = doc.getElementsByClass("comment-list").get(0).select("li");
                    for (Element li : lis) {
                        if (li.toString().contains("摄影")) {
                            Comments comments = new Comments();
                            if (li.select("div").size() > 2) {
                                comments.setContent(li.select("div").get(0).text());
                            } else {
                                comments.setContent(li.select("div").get(1).text());
                            }
                            comments.setRank(li.select("user-info").get(0).getElementsByTag("span").get(1).attr("class").split(" ")[1]);
                            comments.setMarryId(marryInfo.getMarryId());
                            comments.setCommonId(UUID.randomUUID().toString());
                            ThreadUtils.queue_comment.put(comments);
//                   listc.add(comments);
                            parserImg(comments, li.getElementsByClass("shop-photo").get(0));
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parserNeatPage(String result) throws InterruptedException {
        Document doc = Jsoup.parse(result);
        if (doc.toString().contains("下一页")) {
            Elements elements = doc.select("a");
            //抓取页数
            for (Element element : elements) {
                if (element.text().contains("下一页")) {
                    page--;
                    if (page > 0) {
                        String url1 = url + element.attr("href");
                        try {
                            HttpGet get = new HttpGet(url1);
                            CloseableHttpResponse resp = client.execute(get);
                            result = EntityUtils.toString(resp.getEntity());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        doc = Jsoup.parse(result);
                        Elements divs = doc.getElementsByClass("content");
                        for (Element div : divs) {
                            Comments comments = new Comments();
                            if (div.toString().contains("comment-entry")) {
                                comments.setContent(div.select(".comment-entry").get(0).text());
                            }
                            if (div.toString().contains("comment-rst")) {
                                comments.setRank(div.select(".comment-rst").get(0).getElementsByTag("span").get(0).attr("class").split(" ")[1]);
                            }
                            comments.setMarryId(marryInfo.getMarryId());
                            comments.setCommonId(UUID.randomUUID().toString());
                            ThreadUtils.queue_comment.add(comments);

                            if (div.toString().contains("shop-info-gallery")) {
                                parserImg(comments, div.getElementsByClass("shop-info-gallery").get(0));
                            }
                        }
                        //递归
                        parserNeatPage(result);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void parserImg(Comments comments, Element element) throws InterruptedException {
        List<TradeMark> listt = new ArrayList<TradeMark>();
        Elements as = element.select("a");
        if (as == null) {
            System.out.print(comments.getMarryId() + "没有评论照片");
        }

        for (Element a : as) {
            TradeMark tradeMark = new TradeMark();
            tradeMark.setImg(a.attr("src"));
            if (("").equals(a.attr("src"))) {
                tradeMark.setImg(a.attr("data-src"));
            }
            tradeMark.setMarryId(comments.getMarryId());
            tradeMark.setCommonId(comments.getCommonId());
            ThreadUtils.queue_trademark.put(tradeMark);
//            listt.add(tradeMark);
        }

        comments.setImgs(listt);
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

        //System.out.println(UUID.randomUUID().toString());
        //
        //ThreadUtils.queue_dianping.offer("123");
        //ThreadUtils.queue_dianping.offer("232323");
        //
        //System.out.println(JSON.toJSONString(ThreadUtils.queue_dianping));
        ParserFactory.createParser(DianPingParser.class).accessNext(MarryContact.DIANPING_ST, MarryContact.DIANPING_HOST);


    }
}
