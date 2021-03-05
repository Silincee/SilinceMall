package cn.silince.silincemall.order.web;

import cn.silince.silincemall.order.config.AlipayTemplate;
import cn.silince.silincemall.order.service.OrderService;
import cn.silince.silincemall.order.vo.PayVo;
import com.alipay.api.AlipayApiException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-03-03 10:25
 **/
@Controller
public class PayWebController {

    @Resource
    private AlipayTemplate alipayTemplate;

    @Resource
    private OrderService orderService;

    /** 
    * @description: 将支付页让浏览器展示 支付成功以后要跳到用户订单列表页
    */ 
    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

        PayVo payVo=orderService.getOrderPay(orderSn);
        // 返回的是一个页面。将此页面直接交给浏览器就行
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }
}
