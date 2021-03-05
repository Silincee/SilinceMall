package cn.silince.silincemall.coupon.service.impl;

import cn.silince.silincemall.coupon.entity.SeckillSkuRelationEntity;
import cn.silince.silincemall.coupon.service.SeckillSkuRelationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.Query;

import cn.silince.silincemall.coupon.dao.SeckillSessionDao;
import cn.silince.silincemall.coupon.entity.SeckillSessionEntity;
import cn.silince.silincemall.coupon.service.SeckillSessionService;

import javax.annotation.Resource;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Resource
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @description: 查询最近三天的秒杀活动
     */
    @Override
    public List<SeckillSessionEntity> getLates3DaySession() {
        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>()
                .between("start_time", startTime(), endTime()));

        if (list != null && list.size() > 0) {
            List<SeckillSessionEntity> collect = list.stream().map(item -> {
                Long id = item.getId();
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>()
                        .eq("promotion_session_id", id));
                item.setRelationSkus(relationEntities); // 活动涉及到的sku
                return item;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    private String startTime() {
        LocalDate now = LocalDate.now();
        LocalTime min = LocalTime.MIN;
        return LocalDateTime.of(now, min).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String endTime() {
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(2);
        LocalDateTime of = LocalDateTime.of(localDate, LocalTime.MAX);
        return of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}