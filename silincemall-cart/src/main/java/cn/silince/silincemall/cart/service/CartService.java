package cn.silince.silincemall.cart.service;

import cn.silince.silincemall.cart.vo.CartItemVo;
import cn.silince.silincemall.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-25 23:59
 **/
public interface CartService {
    /** 
    * @description: 添加商品到购物车 
    */ 
    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /** 
    * @description: 获取购物车中某个购物项 
    */ 
    CartItemVo getCartItem(Long skuId);

    /** 
    * @description: 获取&合并购物车 
    */ 
    CartVo getCart() throws ExecutionException, InterruptedException;

    /**
     * @description: 清空购物车
     */
    void clearCart(String cartKey);

     /**
     * @description: 勾选购物项
     */
    void checkItem(Long skuId, Integer check);

    /**
     * @description: 改变购物项数量
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * @description: 删除购物项
     */
    void deleteItem(Long skuId);

    /**
     * @description: 查询 当前用户 购物车所有选中的购物项
     */
    List<CartItemVo> getUserCartItems();
}
