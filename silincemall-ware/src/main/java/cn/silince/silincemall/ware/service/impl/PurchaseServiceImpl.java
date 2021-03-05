package cn.silince.silincemall.ware.service.impl;

import cn.silince.common.constant.WareConstant;
import cn.silince.silincemall.ware.entity.PurchaseDetailEntity;
import cn.silince.silincemall.ware.service.PurchaseDetailService;
import cn.silince.silincemall.ware.service.WareSkuService;
import cn.silince.silincemall.ware.vo.MergeVo;
import cn.silince.silincemall.ware.vo.PurchaseDoneVo;
import cn.silince.silincemall.ware.vo.PurchaseItemDoneVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.Query;

import cn.silince.silincemall.ware.dao.PurchaseDao;
import cn.silince.silincemall.ware.entity.PurchaseEntity;
import cn.silince.silincemall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Resource
    private PurchaseDetailService purchaseDetailService;

    @Resource
    private PurchaseService purchaseService;

    @Resource
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                // 0 新建的采购单 1 已分配的采购单但还未被领取
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            // 1 新建一个采购需求
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode()); // 默认采购单的状态
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        // TODO 确认采购单状态是0，1才可以合并
        AtomicBoolean mergeFlag = new AtomicBoolean(true);
        List<Long> items = mergeVo.getItems(); // 待合并的采购需求
        items.stream().map(item->{
            PurchaseDetailEntity byId = purchaseDetailService.getById(item);
            if (byId.getStatus()!=WareConstant.PurchaseDetailStatusEnum.CREATED.getCode()
            || byId.getStatus()!=WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode()){
                log.error("采购需求合并异常");
                mergeFlag.set(false);
            }
            return byId;
        });

        if (mergeFlag.get()){
            Long finalPurchaseId = purchaseId;
            List<PurchaseDetailEntity> collect = items.stream().map(i -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(i);
                detailEntity.setPurchaseId(finalPurchaseId);
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);


            // 更新采购单的时间
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(purchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }

    @Transactional
    @Override
    public void received(List<Long> ids) {
        // 1 确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
                    || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode()); // 设置最新状态 已领取
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        // 2 改变采购单的状态
        this.updateBatchById(collect);

        // 3 改变采购需求的状态
        collect.forEach(item -> {
            List<PurchaseDetailEntity> entities = purchaseDetailService.listDetailByPurchaseId(item.getId());

            List<PurchaseDetailEntity> detailEntities = entities.stream().map(entity -> {
                PurchaseDetailEntity entity1 = new PurchaseDetailEntity();
                entity1.setId(entity.getId());
                entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity1;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo purchaseDoneVo) {
        // 2 改变采购项状态
        Boolean flag = true; // 采购失败标志位
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        ArrayList<PurchaseDetailEntity> updates = new ArrayList<>();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus()==WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag=false;
                detailEntity.setStatus(item.getStatus());
            }else {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                // 3 将成功采购的进行入库
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }

            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);

        // 1 改变采购单状态
        Long id = purchaseDoneVo.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setStatus(flag? WareConstant.PurchaseStatusEnum.FINISH.getCode()
                :WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setId(id);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);




    }
}