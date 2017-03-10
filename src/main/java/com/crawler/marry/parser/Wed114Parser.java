package com.crawler.marry.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crawler.marry.model.Comments;
import com.crawler.marry.model.MarryInfo;
import com.crawler.marry.model.TradeMark;
import com.crawler.marry.parser.factory.ParserFactory;
import com.crawler.marry.util.MarryContact;
import com.crawler.marry.util.ThreadUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by finup on 2017/2/19.
 */
public class Wed114Parser extends Parser {
    //private static final Logger LOG  = LoggerFactory.getLogger(Wed114Parser.class);
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
            try {
                JSONObject jsonObject = parserDiv(div);
                if (jsonObject !=null ) {
                    System.out.println("================================================" +jsonObject);
                    ThreadUtils.queue_wed.put(jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("wed Data: " + JSON.toJSONString(ThreadUtils.queue_wed));
    }

    private JSONObject parserDiv(Element element) {

        JSONObject json = new JSONObject();

        Elements childrens = element.children();
        if (childrens.size() == 2) {
            marryInfo = new MarryInfo();
            marryInfo.setComment(element.getElementsByClass("pinglun").get(0).text().replace("评论",""));
            marryInfo.setHot(element.getElementsByClass("renqi").get(0).text().replace("人气",""));
            marryInfo.setName(element.getElementsByClass("inshoptit").get(0).text());
            marryInfo.setScope(element.getElementsByClass("inaddtxt").get(0).text());
            marryInfo.setPrice(element.getElementsByClass("ismoney").get(0).text().replace("￥","").replace("起",""));

        }
        marryInfo.setMarryId(UUID.randomUUID().toString());

        Element ele = element.getElementsByClass("sheyinlogo").get(0);
        parserComment(ele.toString());

        json.put("MarryInfo",marryInfo);
        json.put("Comments",listc);
        return json;
    }

    @Override
    public void parserComment(String result) {
        Document doc = Jsoup.parse(result);
        try {
            String url = doc.getElementsByTag("a").attr("href");
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse resp = client.execute(get);
            result = EntityUtils.toString(resp.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc = Jsoup.parse(result);
        if(result.contains("查看全部评论")){
            String url = doc.getElementsByClass("morecombtn").get(0).attr("href");
            try {
                HttpGet get = new HttpGet(url);
                CloseableHttpResponse resp = client.execute(get);
                result = EntityUtils.toString(resp.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //解析第一页的评论内容
            doc = Jsoup.parse(result);
            Elements dds = doc.getElementsByClass("pldetailconbox");
            for(Element dd : dds){
                Comments comments = new Comments();
                comments.setContent(dd.getElementsByClass("sccontxt").get(0).text());
                comments.setRank(dd.getElementsByTag("span").get(1).attr("class").split(" ")[0]);
                comments.setMarryId(marryInfo.getMarryId());
                comments.setCommonId(UUID.randomUUID().toString());

                listc.add(comments);
                parserImg(comments,dd.getElementsByClass("ctimg"));
            }
            //解析剩余内容的评论
           parserNeatPage(result);

        }else{
            Elements lis = doc.getElementById("ct_show").select("li");
            for(Element li : lis){
                Comments comments = new Comments();
                comments.setContent(li.select("comc666").text());
                comments.setRank(li.select("shotscore").get(0).getElementsByTag("span").get(1).attr("class").split(" ")[0]);
                comments.setMarryId(marryInfo.getMarryId());
                comments.setCommonId(UUID.randomUUID().toString());

                listc.add(comments);
                parserImg(comments,li.getElementsByClass("ctimg"));
            }
        }
    }

    private void parserNeatPage(String result){
        Document doc = Jsoup.parse(result);
        if(doc.toString().contains("下一页")){
            Elements elements = doc.select("a");
            //抓取页数
            for(Element element : elements){
                if(element.text().contains("下一页")){
                    page --;
                    if(page > 0 ){
                        String url = element.attr("href");
                        try {
                            HttpGet get = new HttpGet("http://ido114.vip.wed114.cn"+url);
                            CloseableHttpResponse resp = client.execute(get);
                            result = EntityUtils.toString(resp.getEntity());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        doc = Jsoup.parse(result);
                        Elements dds = doc.getElementsByClass("pldetailconbox");
                        for(Element dd : dds){
                            Comments comments = new Comments();
                            comments.setContent(dd.getElementsByClass("sccontxt").get(0).text());
                            comments.setRank(dd.getElementsByTag("span").get(1).attr("class").split(" ")[0]);
                            comments.setMarryId(marryInfo.getMarryId());
                            comments.setCommonId(UUID.randomUUID().toString());

                            listc.add(comments);
                            parserImg(comments,dd.getElementsByClass("ctimg"));
                        }
                        //递归
                        parserNeatPage(result);
                    }else{
                        break;
                    }
                }
            }
        }
    }

    private void parserImg(Comments comments,Elements elements){
        List<TradeMark> listt = new ArrayList<TradeMark>();
        if(elements == null){
            System.out.print(comments.getMarryId()+"没有评论照片");
        }

        for(Element ele : elements){
            TradeMark tradeMark = new TradeMark();
            tradeMark.setImg(ele.select("img").attr("src"));
            tradeMark.setMarryId(comments.getMarryId());
            tradeMark.setCommonId(comments.getCommonId());

            listt.add(tradeMark);
        }

        comments.setImgs(listt);
    }
    public static void main(String[] args) throws IOException {

        //File file = new File("/Users/finup/123.html");
        //String result = FileUtils.readFileToString(file);
        //Document document = Jsoup.parse(result);
        //// 评论
        //Element e = document.getElementsByClass("newchoosebox").get(0);
        ////
        //Element element = document.getElementById("ct_show");
        //Element e1 = element.getElementsByTag("ul").get(0);
        //Elements lis  = e1.getElementsByTag("li");
        //System.out.println(lis.size());

//        System.out.println(result);
        ParserFactory.createParser(Wed114Parser.class).accessNext(MarryContact.WED_ST,MarryContact.WED_HOST);
    }

}
