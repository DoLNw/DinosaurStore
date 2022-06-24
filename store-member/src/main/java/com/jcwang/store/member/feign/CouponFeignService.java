package com.jcwang.store.member.feign;

import com.jcwang.store.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 这是一个声明式的远程调用，会从注册中心中找到
 */
@FeignClient("DinosaurStore-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
