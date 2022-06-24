package com.jcwang.store.coupon;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * 如何使用nacos作为配置中心
 *
 * 1 引入依赖
 *
 * 2 创建bootstrap.properties，1当前应用名字，2配置中心服务器地址
 *
 * 3 在配置中心中添加一个默认的数据集（DataID）DinosaurStore-coupon.properties，
 * 默认规则，应用名.properties（本地跟配置中心的，优先使用配置中心）
 * 4 给 应用名.properties添加任何配置
 * 5 动态获取并刷新配置 @RefreshScope @Value("${配置项的名字}")
 *
 *  二、细节
 *  1 命名空间：做配置隔离
 *    默认public（保留空间）
 *    1、开发，测试，生产环境。所以可以创建新的名称空间，比如test、dev、prod
 *      然后bootstrap.properties中配置需要使用哪个命名空间下的配置
 *      spring.cloud.nacos.config.namespace=6b0baddd-4fa5-4e72-8586-3477d5dd0b8b
 *    2、每一个微服务之间互相隔离配置，每一个微服务创建一个自己的命名空间只加载自己命名空间下的配置文件
 *  2 配置集：所有的配置的集合
 *  3 配置集ID：类似于配置文件名，即：应用名.properties
 *  4 配置分组
 *    默认所有的配置集都属于：DEFAUL_TGOURP
 *    比如，双十一、双十二的时候，分别用不同的组名
 *    使用的时候，在bootstrap.properties中加入spring.cloud.nacos.config.group=group名字
 *
 * 整体使用：每个微服务创建自己的命名空间，使用配置分组区分环境，dev、test、prop
 *
 *  三、同时加载多个配置集
 *  bootstrap里面写， 应用名.properties 这个是默认读取的，默认是default分组的，若没有是直接读取本地的了
 *  1 微服务任何配置信息，任何配置文件都可以放在配置中心中
 *  2 只需要在bootstrap.properties说明加载配置中心哪些文件即可
 *  3 @Value，@ConfigurationProperties。。
 *  以前SpringBoot任何方法从配置文件获取值，都能使用
 *  配置中心的优先使用
 */
@EnableDiscoveryClient
@SpringBootApplication
public class DinosaurStoreCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(DinosaurStoreCouponApplication.class, args);
    }
}
