package cn.silince.silincemall.member.web;

import cn.silince.common.utils.R;
import cn.silince.silincemall.member.feign.OrderFeignService;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-03-03 10:59
 **/
@Controller
public class MemberWebController {

@Resource
private OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                  Model model){
        // 查出当前登陆的用户的所有订单列表数据
        HashMap<String, Object> page = new HashMap<>();
        page.put("page",pageNum.toString());
        R r = orderFeignService.listWithItem(page);
        System.out.println(JSON.toJSONString(r));
        model.addAttribute("orders",r);
        return "orderList";
    }
}
