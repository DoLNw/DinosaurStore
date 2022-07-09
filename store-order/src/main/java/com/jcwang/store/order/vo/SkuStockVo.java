package com.jcwang.store.order.vo;

import lombok.Data;

/**
 * @Description: 库存vo
 * @Created: with IntelliJ IDEA.
 * @author: jcwang
 **/

@Data
public class SkuStockVo {

    private Long skuId;

    private Boolean hasStock;

}
