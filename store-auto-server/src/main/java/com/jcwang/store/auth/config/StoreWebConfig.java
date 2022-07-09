package com.jcwang.store.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author dinosaur
 */
@Configuration
public class StoreWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/login.htm").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");

        WebMvcConfigurer.super.addViewControllers(registry);
    }
}
