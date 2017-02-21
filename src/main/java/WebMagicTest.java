import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * Created by finup on 2017/2/17.
 */
public class WebMagicTest implements PageProcessor{
    private Site site = Site.me().setRetrySleepTime(3).setSleepTime(100);


    @Override
    public void process(Page page) {
        page.addTargetRequest((Request) page.getHtml().links().regex("(https://www.dianping.com/wedding/travel/2/\\w+)").all());

        List<Request> list = page.getTargetRequests();
        for (Request request : list ) {
            System.out.println("========================== : " + request.getUrl());
        }
        System.out.println(page.getTargetRequests());
        page.putField("title",page.getHtml().xpath("//*[@id=\"J_boxSearch\"]/div[2]/div[1]/div[2]/div[1]/ul/li[2]/div[2]/h3/a"));


    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String url = "https://www.dianping.com/wedding/travel/2";

        Spider.create(new WebMagicTest()).addUrl(url).thread(1).run();

    }
}
