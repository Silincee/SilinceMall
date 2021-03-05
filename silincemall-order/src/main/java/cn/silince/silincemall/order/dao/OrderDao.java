package cn.silince.silincemall.order.dao;

import cn.silince.silincemall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:13:54
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
    * @description: 修改订单状态
    */
    void updateOrderStatus(@Param("outTradeNo") String outTradeNo,@Param("code") Integer code);
}
