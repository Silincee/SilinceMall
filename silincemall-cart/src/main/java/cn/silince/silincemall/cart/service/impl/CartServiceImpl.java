package cn.silince.silincemall.cart.service.impl;

import cn.silince.common.utils.R;
import cn.silince.silincemall.cart.feign.ProductFeignService;
import cn.silince.silincemall.cart.interceptor.CartInterceptor;
import cn.silince.silincemall.cart.service.CartService;
import cn.silince.silincemall.cart.to.UserInfoTo;
import cn.silince.silincemall.cart.vo.CartItemVo;
import cn.silince.silincemall.cart.vo.CartVo;
import cn.silince.silincemall.cart.vo.SkuInfoVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-25 23:59
 **/
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    // 购物车前缀
    private final String CART_PREFIX = "silincemall:cart:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    /**
     * @description: 获取到要添加商品的购物车，返回购物车的操作对象
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        // 1 判断用户登陆状态选择购物车
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String carKey = "";
        if (userInfoTo.getUserId() != null) {
            // 已登陆 silincemall:cart:3
            carKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            carKey = CART_PREFIX + userInfoTo.getUserKey();
        }

        BoundHashOperations<String, Object, Object> operations
                = redisTemplate.boundHashOps(carKey);// 绑定该key的哈希操作

        return operations;
    }

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 1 获取到要添加商品的购物车
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();


        // ⚠️ 判断是添加还修改商品。修改则只需要修改数量即可
        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            // 购物车无此商品
            CartItemVo cartItemVo = new CartItemVo();
            // 2 远程调用商品服务查询商品详情
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItemVo.setCheck(true);
                cartItemVo.setCount(1);
                cartItemVo.setImage(data.getSkuDefaultImg());
                cartItemVo.setTitle(data.getSkuTitle());
                cartItemVo.setSkuId(skuId);
                cartItemVo.setPrice(data.getPrice());
            }, executor);

            // 3 远程调用查询sku的销售版本信息
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> values = productFeignService.getSkuSaleAttrValuesAsStringList(skuId);
                cartItemVo.setSkuAttr(values);
            }, executor);

            // 4 将商品存入购物车
            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues).get();
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItemVo));

            return cartItemVo;
        } else {
            // 商品已存在，只需要数量叠加即可
            CartItemVo item = JSON.parseObject(res, CartItemVo.class);
            item.setCount(item.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(item));
            return item;
        }


    }


    /**
     * @description: 得到当前购物项
     */
    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String str = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(str, CartItemVo.class);
    }

    /**
     * @description: 获取指定购物车的所有数据
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        List<Object> values = redisTemplate.boundHashOps(cartKey).values();
        if (values != null && values.size() > 0) {
            List<CartItemVo> collect = values.stream()
                    .map(obj -> JSON.parseObject((String) obj, CartItemVo.class))
                    .collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * @description: 清空购物车
     */
    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            // 1 已登陆
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            // 2 如果临时购物车的数据还没有进行合并
            List<CartItemVo> tempCartItems = getCartItems(CART_PREFIX + userInfoTo.getUserKey());
            if (tempCartItems != null) {
                // 3 临时购物车有数据，需要合并
                for (CartItemVo tempCartItem : tempCartItems) {
                    this.addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                // 4 清除临时购物车数据
                this.clearCart(CART_PREFIX + userInfoTo.getUserKey());
            }
            // 5 获取登陆后的购物车(已经合并了临时购物车)
            List<CartItemVo> cartItems = this.getCartItems(cartKey);
            cartVo.setItems(cartItems);

        } else {
            // 未登陆 获取临时购物车的所有购物项目
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItemVo> cartItems = this.getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }
        return cartVo;

    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItemVo cartItem = this.getCartItem(skuId);
        cartItem.setCheck(check == 1 ? true : false);
        this.getCartOps().put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItemVo cartItem = this.getCartItem(skuId);
        cartItem.setCount(num);
        this.getCartOps().put(skuId.toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        this.getCartOps().delete(skuId.toString());
    }

    @Override
    public List<CartItemVo> getUserCartItems() {

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            return getCartItems(CART_PREFIX + userInfoTo.getUserId()).stream()
                    .filter(CartItemVo::getCheck)
                    .map(item -> {
                        // 远程查询商品服务 更新为最新的价格
                        R price = productFeignService.getPrice(item.getSkuId());
                        String data = (String) price.get("data");
                        item.setPrice(new BigDecimal(data));
                        return item;
                    }).collect(Collectors.toList());
        }

    }
}
