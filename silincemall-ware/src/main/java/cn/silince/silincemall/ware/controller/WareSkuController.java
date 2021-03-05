package cn.silince.silincemall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.silince.common.exception.BizCodeEnume;
import cn.silince.common.exception.NoStockException;
import cn.silince.silincemall.ware.vo.LockStockResult;
import cn.silince.silincemall.ware.vo.SkuHasStockVo;
import cn.silince.silincemall.ware.vo.SkuWareLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.silince.silincemall.ware.entity.WareSkuEntity;
import cn.silince.silincemall.ware.service.WareSkuService;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.R;



/**
 * 商品库存
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:31:06
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;


    /**
    * @description: 为订单锁库存
    */
    @PostMapping("/lock/order")
    public  R orderLockStock(@RequestBody SkuWareLockVo vo){
        Boolean stock = null;
        try {
            stock = wareSkuService.orderLockStock(vo);
            return R.ok();
        } catch (NoStockException e) {
            return R.error(BizCodeEnume.NO_STOCK_EXCEPTION.getCode(),BizCodeEnume.NO_STOCK_EXCEPTION.getMsg());
        }

    }

    /**
     * @description: 查询sku是否有库存
     */
    @PostMapping("/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds){
        // 只需要返回sku_id 和 stock
        List<SkuHasStockVo> vos =wareSkuService.getSkuHasStock(skuIds);


        return R.ok().setData(vos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
