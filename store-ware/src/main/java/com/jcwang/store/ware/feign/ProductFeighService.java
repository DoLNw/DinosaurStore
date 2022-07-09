package com.jcwang.store.ware.feign;

import com.jcwang.store.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("DinosaurStore-gateway")
public interface ProductFeighService {

    /**
     * /product/skuinfo/info/{skuId}     这个的话，直接调用Product的服务，不过网关
     * /api/product/skuinfo/info/{skuId} 这个的话，给网关发请求，过网关
     * 1 @FeignClient("DinosaurStore-gateway")
     * 2 /api/product/skuinfo/info/{skuId}
     *
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
