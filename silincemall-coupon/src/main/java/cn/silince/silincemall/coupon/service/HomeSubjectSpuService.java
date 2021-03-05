package cn.silince.silincemall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 18:52:18
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

