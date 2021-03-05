package cn.silince.silincemall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:13:54
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

