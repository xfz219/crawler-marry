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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
    private static final Logger LOG = LoggerFactory.getLogger(JieHunParser.class);

    public JieHunParser() {
        super();
    }

    @Override
    public String parserNext(String result) {
         Element e=  Jsoup.parse(result).getElementsByClass("pager").get(0).
                getElementsByClass("n").get(0);

        if (!e.getElementsByTag("a").isEmpty()){
         return e.getElementsByTag("a").get(0).attr("href");
        }

        return "";

    }

    @Override
    public void parser(String result) {

        Document document = Jsoup.parse(result);
        Element ul_element = document.getElementsByClass("stlist_con").get(0);
        Elements lis_element = ul_element.getElementsByTag("li");
        for (Element e : lis_element) {
            try {

                JSONObject json = parserLi(e);
//                if (marryInfo != null)
////                    JdbcUtils.save(json);
////                    ThreadUtils.queue_jiehun.put(json);
//                System.out.println("===========================" + json);
            } catch (Exception es) {
                es.printStackTrace();
            }

        }
//        System.out.println("jiehun data: " + JSON.toJSONString(ThreadUtils.queue_jiehun));
    }

    private JSONObject parserLi(Element e) {
        JSONObject json = new JSONObject();
        Elements elements = e.children();
        if (elements.size() == 2) {
            marryInfo = new MarryInfo();
            marryInfo.setLevel(e.getElementsByClass("comment").get(0).text());
            marryInfo.setComment(e.getElementsByClass("count").text().replace("已有", "").replace("人点评", ""));
            marryInfo.setName(e.getElementsByClass("storename").get(0).text());
            if (e.getElementsByClass("average_price").size() > 0) {
                marryInfo.setPrice(e.getElementsByClass("average_price").text().replace("￥", ""));
            }
            marryInfo.setMarryId(UUID.randomUUID().toString());
        }

        if (e.getElementsByClass("count").size() > 0) {
            Element ele = e.getElementsByClass("count").get(0);
            String result = "";
            try {
                ThreadUtils.queue.put(marryInfo);
                String url = "http://bj.jiehun.com.cn" + ele.getElementsByTag("a").attr("href");
                HttpGet get = new HttpGet(url);
                CloseableHttpResponse resp = client.execute(get);
                result = EntityUtils.toString(resp.getEntity());
            } catch (Exception es) {
                es.printStackTrace();
            }
            parserComment(result);
        }
//
//        json.put("MarryInfo", marryInfo);
//        json.put("Comments", listc);
        return json;
    }

    @Override
    public void parserComment(String result) {
        Document doc = Jsoup.parse(result);

        Elements dls = doc.getElementsByClass("dpone");
        if (dls!= null && !dls.isEmpty()) {
            for (Element dl : dls) {
                try {
                    Comments comments = new Comments();
                    comments.setContent(dl.getElementsByClass("m-re-content").get(0).text());
                    if (!dl.getElementsByClass("g-um-u-options").isEmpty()) {
                        comments.setRank(dl.getElementsByClass("g-um-u-options").get(0).text());
                    }

                    comments.setMarryId(marryInfo.getMarryId());
                    comments.setCommonId(UUID.randomUUID().toString());
                    System.out.println("json： " + JSON.toJSONString(comments));
                    ThreadUtils.queue_comment.put(comments);

                    if (dl.toString().contains("_jdp_pic")) {
                        parserImg(comments, dl.getElementsByClass("_jdp_pic"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //下一页
            if (result.contains("下一页")) {
                Elements elements = doc.select("a");
                //抓取页数
                for (Element element : elements) {
                    if (element.text().contains("下一页")) {
                        page--;
                        if (page > 0) {
                            String url = element.attr("href");
                            try {
                                HttpGet get = new HttpGet("http://bj.jiehun.com.cn" + url);
                                CloseableHttpResponse resp = client.execute(get);
                                result = EntityUtils.toString(resp.getEntity());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //递归
                            parserComment(result);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }


    private void parserImg(Comments comments, Elements elements) throws InterruptedException {
        List<TradeMark> listt = new ArrayList<TradeMark>();
        if(elements == null){
            System.out.print(comments.getMarryId()+"没有评论照片");
        }

        for(Element ele : elements){
            TradeMark tradeMark = new TradeMark();
            tradeMark.setImg(ele.select("img").attr("hll"));
            tradeMark.setMarryId(comments.getMarryId());
            tradeMark.setCommonId(comments.getCommonId());
            ThreadUtils.queue_trademark.put(tradeMark);
//            listt.add(tradeMark);
        }

        comments.setImgs(listt);
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
