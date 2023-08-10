# 鱼泡-伙伴匹配系统后端

## 数据库表设计

- 标签表

```sql
CREATE TABLE `tag` (
  `id` bigint NOT NULL,
  `name` varchar(10) DEFAULT NULL COMMENT '标签名',
  `category_id` int NOT NULL COMMENT '种类id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '当前状态：0-删除，1-正常',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_tag_name` (`name`) COMMENT '标签名唯一索引',
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
```

- 用户表

```sql
CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '用户id',
  `username` varchar(30) DEFAULT NULL COMMENT '用户名',
  `account` varchar(255) DEFAULT NULL COMMENT '用户账户',
  `avatar_url` varchar(1024) DEFAULT NULL COMMENT '用户头像',
  `gender` tinyint DEFAULT NULL COMMENT '用户性别：0-女，1-男',
  `user_password` varchar(255) NOT NULL COMMENT '密码',
  `tele` varchar(30) DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) DEFAULT NULL COMMENT '用户邮箱',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-删除，1-正常，2-封禁',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_delete` tinyint NOT NULL DEFAULT '1' COMMENT '逻辑删除：0-删除，1-正常',
  `user_role` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '用户角色：0-普通用户，1-管理员',
  `tags` varchar(255) DEFAULT NULL COMMENT '用户拥有的标签',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
```

## 后端整合Swagger+knife4j接口文档

什么是接口文档

- 请求参数
- 响应参数
  - 错误码
- 接口地址
- 接口名称
- 请求方式
- 请求格式
- 备注

谁用？给后端和前端开发人员使用



为什么需要接口文档？

- 有个书面内容（背书或者文档），便于大家参考和查阅，并于**沉淀和维护**，拒绝口口相传。
- 接口文档便于前端和后端对接，前后端联调的**介质**。后端=>接口文档<=前端。
- 好的接口文档能够在线测试、在线调试，可以作为工具提高我们的开发效率。

如何制作接口文档？

- 手写，比如：腾讯文档，Markdown
- 自动化接口文档生成：自动根据项目代码生成完整的文档或在线调试页面。Swagger，Postman

接口文档有哪些技巧？



Swagger原理：

1. 引入依赖

```xml
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-boot-starter</artifactId>
            <version>3.0.0</version>
        </dependency>
```

2. 自定义Swagger配置类

```java
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
            // 标识接口的位置com.yupao.controller
            .apis(RequestHandlerSelectors.basePackage("com.yupao.controller")).paths(PathSelectors.any()).build();
    }

    /**
     * api信息
     * 网页的相关信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("鱼泡--伙伴匹配系统").description("鱼泡--接口文档")
            .termsOfServiceUrl("https://www.github.com/nanaue0312")
            .contact(new Contact("nanaue", "https://blog.nanaue-cdeo.top", "nanaue0312@163.com")).version("1.0")
            .build();
    }
}
```

> TIP:
>
> ​	线上环境千万不要暴露全部接口

3. 启动项目

> SpringBoot2.7+Swagger3.0踩坑
>
> 报错：Failed to start bean 'documentationPluginsBootstrapper'; nested exception is java.lang.NullPointerException
>
> 解决方法：
>
> 在yaml文件中配置：
>
> ```yaml
> spring:
>     mvc:
>     	pathmatch:
>     		matching-strategy: ant_path_matcher
> ```

## 抓取知识星球数据

1. 分析请求列表，找到需要的请求链接

```bash
curl "https://api.zsxq.com/v2/hashtags/48844541281228/topics?count=20" ^
  -H "authority: api.zsxq.com" ^
  -H "accept: application/json, text/plain, */*" ^
  -H "origin: https://wx.zsxq.com" ^
  -H "referer: https://wx.zsxq.com/" ^
  -H "user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.188" ^
  --compressed
```

2. 编写程序，发送请求，获取数据
3. 清洗获取到的数据，存储到数据库中

## 分布式session

当需要多机部署服务时，用户在A服务器上的session无法在另一台服务器的session查找

解决：redis+spring-session-data-redis

1. 引入redis

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2. 引入spring-session-data-redis

```xml
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

3. 修改application.yml配置

```yaml
  session:
    store-type: redis #默认为none
    timeout: 86400 #分钟
```

## 定时任务@EnableScheduling

1. 在主类上使用@EnableScheduling开启服务的定时任务
2. 在需要定时执行的方法上使用@Scheduled()

