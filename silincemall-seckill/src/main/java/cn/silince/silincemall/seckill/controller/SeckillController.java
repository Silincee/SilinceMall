package cn.silince.silincemall.seckill.controller;

import cn.silince.common.utils.R;
import cn.silince.silincemall.seckill.service.SeckillService;
import cn.silince.silincemall.seckill.to.SecKillRedisTo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-03-03 21:44
 **/
@Controller
public class SeckillController {

    @Resource
    private SeckillService seckillService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
    * @description: 返回当前时间可以参与秒杀的商品
    */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SecKillRedisTo> vos=seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    /**
    * @description:  获取某个sku商品的秒杀预告信息
    */
    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SecKillRedisTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }



    /** 
    * @description: 秒杀商品 创建订单号 
    */ 
    @GetMapping("/kill")
    public String secKill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model){

        String orderSn =  seckillService.kill(killId,key,num);

        model.addAttribute("orderSn",orderSn);
        //1、判断是否登录
        return "success";
    }
}
