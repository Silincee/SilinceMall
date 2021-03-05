package cn.silince.silincemall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-28 15:50
 **/
@Data
public class FareVo {
    private MemberAddressVo address;
    private BigDecimal fare;
}
