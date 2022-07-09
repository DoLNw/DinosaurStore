package com.jcwang.store.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Created: with IntelliJ IDEA.
 * @author: jcwang
 **/

@Data
public class FareVo {
    private MemberAddressVo address;

    private BigDecimal fare;

}
