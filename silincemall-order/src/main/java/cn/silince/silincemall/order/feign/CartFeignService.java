package cn.silince.silincemall.order.feign;

import cn.silince.silincemall.order.vo.OrderItemVo;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("silincemall-cart")
public interface CartFeignService {

    /**
     * @description: 查询 当前用户 购物车所有选中的购物项
     */
    @GetMapping("/currentUserCartItems")
    public List<OrderItemVo> getCurrentUserCartItems();
}
