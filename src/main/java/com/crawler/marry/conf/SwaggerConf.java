package com.crawler.marry.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by finup on 2017/2/17.
 */
@Configuration
@EnableSwagger2
public class SwaggerConf {

    @Bean
    public Docket createRestApi(){
        // return new
        // Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
        // .paths(PathSelectors.regex("/credit/.*")).build();
        return new Docket(DocumentationType.SWAGGER_2).groupName("demo").genericModelSubstitutes(DeferredResult.class)
                // .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false).forCodeGeneration(true).pathMapping("/")// base，最终调用接口后会和paths拼接在一起
                .select().paths((PathSelectors.regex("/marry.*")))// 过滤的接口
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("婚恋网抓取程序RESTful APIS")
                .description("抓取婚恋信息的api just so so")
                .contact("xfz")
                .version("1.0.0")
                .build();

    }
}
