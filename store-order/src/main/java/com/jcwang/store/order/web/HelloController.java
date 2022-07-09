package com.jcwang.store.order.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcwang.store.order.entity.OrderEntity;
import com.jcwang.store.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page) {
        return page;
    }

    @GetMapping("/test/timezone")
    @ResponseBody
    public String testTimezone() {
        return String.valueOf(orderService.list(new QueryWrapper<OrderEntity>().between("id", 30, 50)));
    }
}
