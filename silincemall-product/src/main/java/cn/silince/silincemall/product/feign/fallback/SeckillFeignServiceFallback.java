package cn.silince.silincemall.product.feign.fallback;

import cn.silince.common.exception.BizCodeEnume;
import cn.silince.common.utils.R;
import cn.silince.silincemall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @program: SilinceMall
 * @description: feign服务熔断
 * @author: Silince
 * @create: 2021-03-04 19:59
 **/
@Slf4j
@Component
public class SeckillFeignServiceFallback implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("熔断保护已开启....");
        return R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(),BizCodeEnume.TOO_MANY_REQUEST.getMsg());
    }
}
