package com.jcwang.store.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Configuration
public class StoreFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            /**
             *
             * @param requestTemplate 新请求，真正要发出去的请求
             */
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // 拿到OrderWenController中刚进来的请求
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                // 如果空，比如查询库存，不需要带cookie请求
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest(); // 老请求 此处，之前从toTrade中同步进来的带有请求头的cookie等的信息拿到了

                    // 若有，比如查询用户地址，还有用户选择的商品，需要用户，所以要带有cookie
                    if (request != null) {
                        // 给远程调用的方法同步请求头数据
                        String cookie = request.getHeader("Cookie");
                        // 给新请求同步了老请求的cookie
                        requestTemplate.header("Cookie", cookie);
                    }
                }

//                System.out.println("feign远程之前先进行RequestInterceptor");
            }
        };
    }
}
