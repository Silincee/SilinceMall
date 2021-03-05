package cn.silince.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: SilinceMall
 * @description: 用于远程调用传输的spuboundsto
 * @author: Silince
 * @create: 2021-02-14 23:49
 **/
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
