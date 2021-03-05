package cn.silince.silincemall.order.feign;

import cn.silince.silincemall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-27 18:13
 **/
@FeignClient("silincemall-member")
public interface MemberFeignService {

    /**
     * @description: 返回会员收货地址
     */
    @RequestMapping("/member/memberreceiveaddress/{memberId}/addresses")
    public List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
}
