package cn.silince.silincemall.cart.controller;

import cn.silince.common.constant.AuthServerConstant;
import cn.silince.silincemall.cart.interceptor.CartInterceptor;
import cn.silince.silincemall.cart.service.CartService;
import cn.silince.silincemall.cart.to.UserInfoTo;
import cn.silince.silincemall.cart.vo.CartItemVo;
import cn.silince.silincemall.cart.vo.CartVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-26 00:03
 **/
@Controller
public class CartController {

    @Resource
    private CartService cartService;

    /**
    * @description: 查询 当前用户 购物车所有选中的购物项
    */
    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItemVo> getCurrentUserCartItems(){
        return cartService.getUserCartItems();
    }

    /**
    * @description: 跳转到购物车列表页
     * 临时购物车：
     *   浏览器存在一个cookie： user-key标识用户身份，一个月后过期；
     *   如果第一次使用jd购物车功能，都会给一个临时的用户身份。
     *   浏览器以后保存，每次访问都会带上这个cookie
     * 登陆：存在session
     * 没登陆：按照cookie里面带来的user-key来做
     * 第一次：如果没有临时用户，帮忙创建一个临时用户。
    */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {

        CartVo cart=cartService.getCart();
        model.addAttribute("cart",cart);

        return "cartList";
    }

    /**
    * @description: 添加商品到购物车
     *  RedirectAttributes ra:
     *      ra.addFlashAttribute("skuId",skuId); 将数据放在session里面可以在页面取出，但是只能取一次
     *      ra.addAttribute("skuId",skuId); 将数据放在url后面
    */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId")Long skuId
            , @RequestParam("num") Integer num, RedirectAttributes ra) throws ExecutionException, InterruptedException {
        CartItemVo cartItem =cartService.addToCart(skuId,num); // 返回商品详细信息

        ra.addAttribute("skuId",skuId);

        return "redirect:http://cart.silincemall.com/addToCartSuccess.html";
    }

    /**
    * @description: 跳转到成功页(防止 添加商品到购物车 重复刷新，需要重定向来到达成功页)
    */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId")Long skuId,Model model) {
        // 重定向到成功页面，再次查询数据即可
        CartItemVo cartItem=cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);

        return "success";
    }

    /**
    * @description: 选择购物项
    */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId")Long skuId,@RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.silincemall.com/cart.html";
    }

    /**
    * @description: 改变购物项数量
    */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId")Long skuId,@RequestParam("num") Integer num){
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.silincemall.com/cart.html";
    }

    /**
     * @description: 删除购物项
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.silincemall.com/cart.html";
    }

}
