package com.jcwang.store.ware.vo;

import lombok.Data;


@Data
public class PurchaseItemDoneVo {

    // ,   // 此处1表示采购项（采购需求）1，
    /**
     * { "itemId": 1, "status": 3, "reason": ""}
     * itemId 此处1表示采购项（采购需求）1
     * status 3表示购买完成
     * renson 完成或者失败原因
     */
    private Long itemId;
    private Integer status;
    private String reason;
}
