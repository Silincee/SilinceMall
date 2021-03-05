package cn.silince.silincemall.order.web;

import cn.silince.silincemall.order.service.OrderService;
import cn.silince.silincemall.order.vo.OrderConfirmVo;
import cn.silince.silincemall.order.vo.OrderSubmitVo;
import cn.silince.silincemall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-27 16:27
 **/
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * @description: 返回订单确认需要用的数据
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();

        model.addAttribute("orderConfirmData", confirmVo);
        // 展示订单确认的数据
        return "confirm";
    }
    
    /** 
    * @description: 根据返回的数据提交生成订单
    */ 
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        // 下单： 去创建订单，验证令牌，验证价格，锁定库存
        SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);


        if (responseVo.getCode()==0){
            // 下单成功，跳转到字符选择页
            model.addAttribute("submitOrderResp",responseVo);
            return "pay";
        }else {
            // 下单失败返回订单确认页重新确认订单信息
            String msg = "下单失败；";
            switch (responseVo.getCode()){
                case 1:  msg += "订单信息过期，请刷新再次提交"; break;
                case 2: msg+= "订单商品价格发生变化，请确认后再次提交"; break;
                case 3: msg+="库存锁定失败，商品库存不足"; break;
            }
            redirectAttributes.addFlashAttribute("msg",msg);
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }
}
