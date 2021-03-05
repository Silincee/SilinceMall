package cn.silince.silincemall.product.service.impl;

import cn.silince.silincemall.product.service.CategoryBrandRelationService;
import cn.silince.silincemall.product.vo.Catelog2Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.Query;

import cn.silince.silincemall.product.dao.CategoryDao;
import cn.silince.silincemall.product.entity.CategoryEntity;
import cn.silince.silincemall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    CategoryDao categoryDao;

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @description: 查出所有分类以及子分类, 以树形结构组装起来
     * @param: []
     * @return: java.util.List<cn.silince.silincemall.product.entity.CategoryEntity>
     * @author: Silince
     * @date: 2/5/21
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1 查出所有分类
        List<CategoryEntity> entities = categoryDao.selectList(null);

        //2 组装成父子的树形结构
        //2.1 找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildren(menu, entities)); //获取当前菜单的所有子菜单
                    return menu;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }) //排序
                .collect(Collectors.toList());


        return level1Menus;
    }

    /**
     * @description: 递归查找所有菜单的子菜单
     * @param: [root, all]
     * @return: [当前菜单, 所有菜单]
     * @author: Silince
     * @date: 2/5/21
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            // 递归寻找子菜单
            categoryEntity.setChildren(getChildren(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> { //菜单排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return children;

    }

    /**
     * @description: 删除 ，删除前检查当前删除的菜单是否被别的地方引用
     * @param: [asList]
     * @return: void
     * @author: Silince
     * @date: 2/5/21
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {

        // TODO 检查当前删除的菜单是否被别的地方引用

        // 逻辑删除
        categoryDao.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        ArrayList<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * @description: 级联更新所有关联的数据
     * @CacheEvict 缓存失效模式 更新后删除缓存中的数据，等待下次主动查询更新
     */
    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {

        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());


    }

    /**
     * @description: 每一个需要缓存的数据我们都来指定要放到哪个名字的缓存下【缓存的分区-按照业务类型区分】
     * 代表当前方法的结果需要缓存，如果缓存中有，方法不用调用。如果缓存中没有就会调用方法并把结果放入缓存中。
     */
    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>()
                .eq("parent_cid", 0));
        return categoryEntities;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parentCid;
        }).collect(Collectors.toList());
        return collect;
    }


    /**
     * @description: 分布式锁Redisson优化
     * 1. 缓存数据一致性问题： 缓存里面的数据如何和数据库保持一致
     * 1） 双写模式
     * 2） 失效模式
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        // 1. 锁的名字决定了锁的粒度，越细越快
        // 锁的粒度：具体缓存的是否个数据，11-号商品：product-11-lock
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getCatalogJsonFromDbByOnce();
        } finally {
            lock.unlock();
        }

        return dataFromDb;


    }


    /**
     * @description: 三级分类(优化业务) 使用redis缓存优化 Spring Cache + 分布式读写锁Redisson
     */
    @Cacheable(value = "category", key = "#root.method.name", sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        System.out.println("查询了数据库");
        // 查出所有
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);
        // 1 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);
        // 2 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(),
                v -> {
                    // 2.1 每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
                    // 2.2 封装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo =
                                    new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                            // 2.3 找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> level3Catelog = getParentCid(selectList, l2.getCatId());
                            if (level3Catelog != null) {
                                List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                                    Catelog2Vo.Catelog3Vo catelog3Vo =
                                            new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catelog3Vo;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }


                            return catelog2Vo;
                        }).collect(Collectors.toList());

                    }
                    return catelog2Vos;
                }));


        return parent_cid;
    }

    /**
     * @description: 三级分类(优化业务) 使用redis缓存优化 / 加锁：解决缓存击穿 Redisson
     */
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        // TODO 会产生对堆内存溢出 OutofDirectMemoryError

        /**
         * 1. 空结果缓存：解决缓存穿透
         * 2. 设置过期时间(加随机值)：解决缓存雪崩
         * 3. 加锁：解决缓存击穿 Redisson
         * */

        // 1. 加入缓存逻辑，缓存中存的数据是json字符串(跨平台兼容)
        String catalogJSON = stringRedisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            // 2 缓存中没有数据 需要查询数据库
            getCatalogJsonFromDbWithRedissonLock();
        }
        // 4 转为我们指定的对象
        Map<String, List<Catelog2Vo>> result =
                JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
        return result;
    }

    /**
     * @description: 三级分类(优化业务) 将数据库的多次查询变为一次
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbByOnce() {
        // 查出所有
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        // 1 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParentCid(selectList, 0L);
        // 2 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(),
                v -> {
                    // 2.1 每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
                    // 2.2 封装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo =
                                    new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                            // 2.3 找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> level3Catelog = getParentCid(selectList, l2.getCatId());
                            if (level3Catelog != null) {
                                List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                                    Catelog2Vo.Catelog3Vo catelog3Vo =
                                            new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catelog3Vo;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }


                            return catelog2Vo;
                        }).collect(Collectors.toList());

                    }
                    return catelog2Vos;
                }));

        // 3 查到的数据再次放入缓存，将对象转为json放在缓存
        String s = JSON.toJSONString(parent_cid);
        stringRedisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);

        return parent_cid;
    }


    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
        // 1 查出所有1级分类
        List<CategoryEntity> level1Categorys = this.getLevel1Categorys();
        // 2 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(),
                v -> {
                    // 2.1 每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = this.baseMapper.selectList(
                            new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
                    // 2.2 封装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());

                            // 2.3 找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> level3Catelog = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                            if (level3Catelog != null) {
                                List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catelog3Vo;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }


                            return catelog2Vo;
                        }).collect(Collectors.toList());

                    }
                    return catelog2Vos;
                }));

        return parent_cid;
    }
}