package com.yupao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@MapperScan("com.yupao.mapper")
@EnableOpenApi
@EnableScheduling
public class YupaoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YupaoBackendApplication.class, args);
    }

}
