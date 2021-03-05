package cn.silince.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @program: SilinceMall
 * @description: 消息队列 库存锁定to
 * @author: Silince
 * @create: 2021-03-02 16:57
 **/
@Data
public class StockLockedTo {

    private Long id; // 库存工作单的id
    private StockDetailTo detail; // 工作单详情
}
