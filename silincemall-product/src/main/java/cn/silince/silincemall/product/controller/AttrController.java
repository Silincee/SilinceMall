package cn.silince.silincemall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.silince.silincemall.product.entity.ProductAttrValueEntity;
import cn.silince.silincemall.product.service.ProductAttrValueService;
import cn.silince.silincemall.product.vo.AttrRespVo;
import cn.silince.silincemall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.silince.silincemall.product.entity.AttrEntity;
import cn.silince.silincemall.product.service.AttrService;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.R;

import javax.annotation.Resource;


/**
 * 商品属性
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-10 20:10:33
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Resource
    private ProductAttrValueService productAttrValueService;

    /**
     * @description: 获取spu规格  /product/attr/base/listforspu/{spuId}
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities =productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data",entities);
    }


    /** 
    * @description:
     *  1. 获取分类规格参数 /product/attr/base/list/{catelogId}
     *  2. 销售属性维护 /product/attr/sale/list/{catelogId}
    */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId")Long catelogId,
                          @PathVariable("attrType") String type){


        PageUtils page =attrService.queryBaseAttrPage(params,catelogId,type);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 查询属性详情
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo =attrService.getAttrInfo(attrId);

        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改 规格参数修改请求
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 修改 API-23、修改商品规格
     */
    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId")Long spuId
            ,@RequestBody  List<ProductAttrValueEntity> entities){

        productAttrValueService.updateSpuAttr(spuId,entities);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
