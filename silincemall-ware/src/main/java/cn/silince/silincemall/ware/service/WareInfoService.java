package cn.silince.silincemall.ware.service;

import cn.silince.silincemall.ware.vo.FareVo;
import cn.silince.silincemall.ware.vo.SkuHasStockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.ware.entity.WareInfoEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:31:06
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /** 
    * @description: 根据收货地址计算运费 
    */
    FareVo getFare(Long addrId);
}

