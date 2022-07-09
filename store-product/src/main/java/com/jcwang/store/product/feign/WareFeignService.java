package com.jcwang.store.product.feign;

import com.jcwang.store.to.SkuHasStockVo;
import com.jcwang.store.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("DinosaurStore-ware")
public interface WareFeignService {

    /**
     * 1 远程调用的，拿到R，再取出来麻烦，所以可以加个范型mHashMap不支持
     * 2 controller中直接返回想要的结果
     * 3 自己封装解析结果
     * @param skuIds
     * @return
     */

    @PostMapping("/ware/waresku/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);

}
