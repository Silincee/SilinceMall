package cn.silince.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-15 00:07
 **/
@Data
public class SkuReductionTo {
    private Long SkuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
