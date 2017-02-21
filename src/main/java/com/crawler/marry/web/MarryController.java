package com.crawler.marry.web;

/**
 * Created by finup on 2017/2/17.
 */

import com.crawler.marry.manager.MarryFetcher;
import com.crawler.marry.model.CrawlerTask;
import com.crawler.marry.model.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 婚恋抓取入口
 */

@RestController
@RequestMapping (value = "/marry")
@Api(value = "marry相关接口信息", protocols = "JSON")
public class MarryController {

    @Autowired
    private MarryFetcher marryFetcher;

    @RequestMapping(value = "subTask")
    @ApiOperation(value="提交抓取任务", notes="根据提交的任务去对应的url下抓取")
    @ApiImplicitParam(name = "task", value = "抓取任务", required = true, dataType = "CrawlerTask")
    public Response singSubTask(@RequestParam (required = true) @ApiParam (name = "task" ,value = "任务model" ,required = true)CrawlerTask task,
                                @RequestParam (required = true) @ApiParam (name = "requestId" ,value = "请求号" ,required = true)String reqestId) {

        return new Response(true,Response.SUC_CODE);
    }
    @RequestMapping(value = "/getSub")
    public void getSub(){
        marryFetcher.insert();
    }
}
