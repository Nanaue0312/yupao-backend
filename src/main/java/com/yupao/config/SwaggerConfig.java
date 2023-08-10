package com.yupao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
            // 标识接口的位置
            .apis(RequestHandlerSelectors.basePackage("com.yupao.controller")).paths(PathSelectors.any()).build();
    }

    /**
     * api信息
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("鱼泡--伙伴匹配系统").description("鱼泡--接口文档")
            .termsOfServiceUrl("https://www.github.com/nanaue0312")
            .contact(new Contact("nanaue", "https://blog.nanaue-cdeo.top", "nanaue0312@163.com")).version("1.0")
            .build();
    }
}
