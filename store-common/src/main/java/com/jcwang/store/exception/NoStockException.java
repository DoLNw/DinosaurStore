package com.jcwang.store.exception;

/**
 * @author dinosaur
 */
public class NoStockException extends RuntimeException {

    private Long skuId;
    private String messsage;

    public NoStockException(Long skuId) {
        super("商品id：" + skuId + "没有足够的库存了");
    }

    public NoStockException(String messsage) {
        super(messsage);
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }
}
