package com.jcwang.store.product;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 整合mybatisplus
 * 导入依赖
 * 配置mysql数据源
 * 配置mubatisplus，使用mapperscan扫描mapper包，告诉mybatisplus，sql语句在哪里（yml中）
 *
 *
 * 2 逻辑删除
 *   1 配置全局的逻辑删除规则（省略）
 *   2 配置逻辑删除的组建（省略）
 *   3 加上逻辑删除注解@TableLogic
 *
 * 3 JSR303校验注解
 *   1 给bean加标注注解：给javax.validation.constraints,并定义自己的message提示
 *   2 告诉Spring需要开启校验@Valid
 *      效果：校验错误以后会有默认响应
 *   3 给校验的bean后面紧跟一个BindResult，就可以获取到校验的结果，就可以封装响应
 *   4 分组校验（多场景的复杂校验）
 *      1 @NotBlank(message = "", gourps = {UpdateGroup.class})
 *      2 告诉需要验证哪一个组，@Validated({AddGroup.class})
 *      3 默认没有指定分组的校验注解，在分组校验情况下@Validated({AddGroup.class})不生效，只会在@Valid生效。
 *   5 自定义校验分组
 *      1 自己编写一个自定义的校验注解
 *      2 编写一个自定义的校验器
 *      3 关联自定义的校验器和自定义的校验注解
 *
 *      @Documented
 *      @Constraint(
 *              validatedBy = {ListValueConstraintValidator.class}
 *      )
 *      @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
 *      @Retention(RetentionPolicy.RUNTIME)
 *      public @interface ListValue {
 *
 * 4 统一的异常处理
 * @ControllerAdvice
 *    1 编写异常处理类，使用@ControllerAdvice
 *    2 使用ExceptionHandler标注方法可以处理的异常
 *
 * 5 模版引擎
 *    1 thymeleaf-starter 关闭缓存
 *    2 静态资源都放在static下面就可以按照路径直接访问
 *    3 页面放在templates，直接访问
 *      因为springboot访问项目的时候，默认会找index
 *      通过model将数据传给html之后，首先在html中加入thymeleaf命名空间，然后用${}即可取出
 *    4 页面修改实时更新，导入依赖spring-boot-devtools，注意optional要为true，改好后编译即可
 *
 *  6  整合redis
 *    1 引入data-redis-starter
 *    2 简单配置redis的host等信息
 *    3 使用springboot自动配置好的stringredistemplate来操作redis
 *
 *  7 redisson分布式锁，引入依赖
 *    redission
 *
 *  8 整合springcache引入依赖
 *    1 引入cache依赖sprint-boot-starter-cache，spring-boot-starter-data-redis
 *    2 缓存自动配置
 *      1 CacheAuroConfiguration会导入RedisCacheConfiguration；
 *        自动配置好了缓存管理器RedisCacheManager
 *      2 application.properties中配置使用redis作为缓存
 *      3 使用缓存
 *        @Cacheable
 *        @CacheEvict
 *        @CachePut
 *        @Caching
 *        @CacheConfig
 *        1 开启缓存功能 @EnableCaching
 *        2 只需要使用注解就能完成缓存操作
 *        比如getLevel1Categorys @Cacheable（"category"）
 *        // 代表当前方法的结果需要缓存，如果缓存中有，方法不调用；如果缓存中没有，会调用方法，最后将方法的结果返回，默认时间-1，永不过期
 *
 *        所以要自定义操作
 *        1 指定生成的缓存使用的key，key属性指定，接收一个spgl
 *        2 指定缓存的数据的存活时间，配置文件中修改ttl，毫秒
 *        3 将数据保存为json格式，自定义缓存管理器
 *
 *      4 想修改缓存的配置，只要给容器中放一个RedisCacheConfiguration即可，遵循链式编程
 *
 */

@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.jcwang.store.product.feign")
@EnableDiscoveryClient
@MapperScan("com.jcwang.store.product.dao")
@SpringBootApplication
public class DinosaurStoreProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(DinosaurStoreProductApplication.class, args);
    }
}
