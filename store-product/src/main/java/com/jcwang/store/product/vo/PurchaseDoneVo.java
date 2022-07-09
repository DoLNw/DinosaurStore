package com.jcwang.store.product.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dinosaur
 */
@Data
public class PurchaseDoneVo {

    /**
     * 采购单
     */
    @NotNull
    private Long id;

    @NotNull
    private List<PurchaseItemDoneVo> items;
}
