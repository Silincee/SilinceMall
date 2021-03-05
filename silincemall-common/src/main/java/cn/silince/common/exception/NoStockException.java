package cn.silince.common.exception;

/**
 * @program: SilinceMall
 * @description: 库存不足异常
 * @author: Silince
 * @create: 2021-02-28 22:29
 **/
public class NoStockException extends RuntimeException {

    private Long skuId;

    public NoStockException(Long skuId) {
        super("商品id: "+skuId+"的商品没有足够的库存了");
    }

    public NoStockException(String message){
        super(message);
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
