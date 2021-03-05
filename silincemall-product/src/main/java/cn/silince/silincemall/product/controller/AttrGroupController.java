package cn.silince.silincemall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.silince.silincemall.product.dao.CategoryDao;
import cn.silince.silincemall.product.entity.AttrEntity;
import cn.silince.silincemall.product.service.AttrAttrgroupRelationService;
import cn.silince.silincemall.product.service.AttrService;
import cn.silince.silincemall.product.service.CategoryService;
import cn.silince.silincemall.product.vo.AttrGroupRelationVo;
import cn.silince.silincemall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import cn.silince.silincemall.product.entity.AttrGroupEntity;
import cn.silince.silincemall.product.service.AttrGroupService;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.R;

import javax.annotation.Resource;
import javax.ws.rs.POST;


/**
 * 属性分组
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 17:00:18
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Resource
    private AttrService attrService;

    @Resource
    private AttrAttrgroupRelationService attrAttrgroupRelationService;


    /** 
    * @description: 商品系统17-获取分类下所有分组和关联属性 /product/attrgroup/{catelogId}/withattr
    */
    @GetMapping("/{catelogId}/withattr")
    public  R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){
        // 1 查出当前分类下的所有属性分组
        // 2 查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> vos=attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",vos);
    }
    
    /** 
    * @description: 添加属性与分组关联关系 /product/attrgroup/attr/relation
    */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){

        attrAttrgroupRelationService.saveBatch(vos);
        return R.ok();
    }
    
    /** 
    * @description: 获取属性分组的关联的所有属性  /product/attrgroup/{attrgroupId}/attr/relation
    */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId")Long attrgroupId){
        List<AttrEntity> entities=attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entities);
    }

    /**
    * @description: 获取没有属性分组的关联的所有属性 /product/attrgroup/{attrgroupId}/noattr/relation
    */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId")Long attrgroupId,
                            @RequestParam Map<String, Object> params){
        PageUtils page=attrService.getNoRelationAttr(params,attrgroupId);
        return R.ok().put("page",page);
    }
    
    /** 
    * @description: 删除属性与分组的关联关系 /product/attrgroup/attr/relation/delete
    */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }

    /**
     * 列表 获取分类属性分组
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long[] path=categoryService.findCatelogPath(catelogId);

        attrGroup.setCatelogPath(path);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
