package cn.silince.silincemall.order.feign;

import cn.silince.common.utils.R;
import cn.silince.silincemall.order.vo.SkuWareLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("silincemall-ware")
public interface WmsFeignService {

    /**
     * @description: 查询sku是否有库存
     */
    @PostMapping("/ware/waresku/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);

    /**
     * @description: 根据收货地址计算运费
     */
    @GetMapping("/ware/wareinfo/fare")
    public R getFare(@RequestParam("addrId")Long addrId);

    /**
     * @description: 为订单锁库存
     */
    @PostMapping("/ware/waresku/lock/order")
    public R orderLockStock(@RequestBody SkuWareLockVo vo);
}
