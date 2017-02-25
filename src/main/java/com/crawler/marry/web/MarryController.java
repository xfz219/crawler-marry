package com.crawler.marry.web;

/**
 * Created by finup on 2017/2/17.
 */

import com.crawler.marry.manager.MarryFetcher;
import com.crawler.marry.model.CrawlerTask;
import com.crawler.marry.model.Response;
import com.crawler.marry.parser.DianPingParser;
import com.crawler.marry.parser.factory.ParserFactory;
import com.crawler.marry.util.MarryContact;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 婚恋抓取入口
 */

@RestController
@RequestMapping (value = "/marry")
@Api(value = "marry相关接口信息", protocols = "JSON")
public class MarryController {

    @Autowired
    private MarryFetcher marryFetcher;

    @RequestMapping(value = "/subTask" ,method = RequestMethod.POST)
    @ApiOperation(value="提交抓取任务", notes="根据提交的任务去对应的url下抓取")
    @ApiImplicitParam(name = "task", value = "抓取任务", required = true)
    public Response singSubTask(@RequestBody(required = true) @ApiParam (name = "task" ,value = "任务model" ,required = true)String task,
                                @ApiParam (name = "requestId" ,value = "请求号" ,required = true)String reqestId) {
        try {

            marryFetcher.fetcher(task);
//            ParserFactory.createParser(DianPingParser.class).accessNext(MarryContact.DIANPING_ST,MarryContact.DIANPING_HOST);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Response(true,Response.SUC_CODE);
    }

}
