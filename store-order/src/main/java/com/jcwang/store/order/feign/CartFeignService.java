package com.jcwang.store.order.feign;

import com.jcwang.store.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient("DinosaurStore-cart")
public interface CartFeignService {

    /**
     * 获取当前用户的购物车商品项
     * @return
     */
    @GetMapping(value = "/currentUserCartItems")
    public List<OrderItemVo> getCurrentCartItems();
}
