package cn.silince.silincemall.seckill.service.impl;

import cn.silince.common.to.mq.SeckillOrderTo;
import cn.silince.common.utils.R;
import cn.silince.common.vo.MemberRespVo;
import cn.silince.silincemall.seckill.interceptor.LoginUserInterceptor;
import cn.silince.silincemall.seckill.to.SecKillRedisTo;
import cn.silince.silincemall.seckill.feign.CouponFeignService;
import cn.silince.silincemall.seckill.feign.ProductFeignService;
import cn.silince.silincemall.seckill.service.SeckillService;
import cn.silince.silincemall.seckill.vo.SeckillSessionsWithSkus;
import cn.silince.silincemall.seckill.vo.SkuInfoVo;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sun.deploy.security.BlockedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-03-03 17:07
 **/
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";

    // 用于实现秒杀的分布式信号量
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";// + 商品随机码

    @Resource
    private CouponFeignService couponFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * @description: 每天晚上3点；上架最近三天需要秒杀的商品。
     */
    @Override
    public void uploadSeckullSkuLatest3Days() {
        // 1 扫描最近三天需要参与秒杀的活动
        R session = couponFeignService.getLates3DaySession();
        if (session.getCode() == 0) {
            // 上架商品
            List<SeckillSessionsWithSkus> sessionData =
                    session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
                    });
            // 2 缓存数据到redis中：活动信息和活动的商品信息
            // 缓存活动信息
            saveSessionInfos(sessionData);
            // 缓存活动的关联商品信息
            saveSessionSkuInfos(sessionData);
        }
    }


    /**
     * @description: 缓存活动信息
     */
    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions) {
        if (sessions!=null&&sessions.size()>0){
            sessions.stream().forEach(session -> {
                long startTime = session.getCreateTime().getTime();
                long endTime = session.getEndTime().getTime();
                String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
                // 幂等性处理，保存时先判断是否存在
                if (!redisTemplate.hasKey(key)) {
                    List<String> collect = session.getRelationSkus().stream().map(item -> {
                        return item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString();
                    }).collect(Collectors.toList());
                    // key seckill:sessions:startTime_endTime value:skuId...
                    redisTemplate.opsForList().leftPushAll(key, collect);
                }
            });
        }
    }

    /**
     * @description: 缓存活动的关联商品信息
     */
    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions) {


        sessions.stream().forEach(session -> {
            // 绑定hash操作
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                // 4 设置商品的随机码 防止被攻击(seckill?skuId=1&key=asadf)
                String token = UUID.randomUUID().toString().replace("-", "");

                // 幂等性处理，保存时先判断是否存在 key:sessionId(活动场次id)_skuId
                if (!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString()
                        + "_" + seckillSkuVo.getSkuId().toString())) {
                    // 缓存商品
                    SecKillRedisTo redisTo = new SecKillRedisTo();
                    // 1 sku的基本数据
                    R skuInfo = productFeignService.skuInfo(seckillSkuVo.getSkuId());
                    if (skuInfo.getCode() == 0) {
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfo(info);
                    }

                    // 2 sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);

                    // 3 设置当前商品的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());


                    redisTo.setRandomCode(token);


                    ops.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(), JSON.toJSONString(redisTo));

                    // 如果当前这个场次的商品的库存信息已经保存，就不需要上架了
                    // 5 限流 引入分布式信号量 商品可以秒杀的数量作为信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount()); // 信号量的值为商品的库存
                }

            });
        });
    }

    /**
     * @description: 返回当前时间可以参与秒杀的商品 Sentinel 自定义受保护的资源
     */
    @Override
    public List<SecKillRedisTo> getCurrentSeckillSkus() {

        // 1 确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();

        try(Entry entry = SphU.entry("seckillSkus")){
            Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
            for (String key : keys) {
                // seckill:sessions:1614757872000_1614740400000
                String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
                String[] s = replace.split("_");
                Long start = Long.parseLong(s[0]);
                Long end = Long.parseLong(s[1]);

                if (time >= start && time <= end) {
                    // 2 获取这个秒杀场次关联的所有商品信息
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hashOps
                            = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                    List<String> list = hashOps.multiGet(range);
                    if (list != null) {
                        List<SecKillRedisTo> collect = list.stream().map(item -> {
                            SecKillRedisTo redis = JSON.parseObject((String) item, SecKillRedisTo.class);
                            //redis.setRandomCode(null); 当前秒杀开始就需要随机码
                            return redis;
                        }).collect(Collectors.toList());
                        return collect;
                    }
                    break;
                }
            }

        }catch ( BlockException e){
        log.error("资源被限流，{}",e.getMessage());
        }
        return null;

    }

    /**
     * @description: 获取某个sku商品的秒杀预告信息
     */
    @Override
    public SecKillRedisTo getSkuSeckillInfo(Long skuId) {
        // 1 找到所有需要参与秒杀的key信息
        BoundHashOperations<String, String, String> hashOps
                = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                // 2_3
                if (Pattern.matches(regx, key)) {
                    SecKillRedisTo skuRedisTo = JSON.parseObject(hashOps.get(key), SecKillRedisTo.class);
                    // 秒杀活动开始后 才允许携带秒杀随机码
                    long current = new Date().getTime();
                    if (current >= skuRedisTo.getStartTime() && current <= skuRedisTo.getEndTime()) {
                    } else {
                        skuRedisTo.setRandomCode(null);
                    }
                    return skuRedisTo;
                }
            }
        }
        return null;
    }


    /**
     * @description: 秒杀商品 创建订单号
     */
    // TODO 上架秒杀商品的时候，每一个数据都有过期时间
    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();

        // 1 获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps =
                redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

        String json = hashOps.get(killId);
        if (StringUtils.isEmpty(json)) {
            return null;
        } else {
            SecKillRedisTo redis = JSON.parseObject(json, SecKillRedisTo.class);
            // 2 校验合法性
            // 2-1 校验时间合法性
            Long startTime = redis.getStartTime();
            Long endTime = redis.getEndTime();
            long time = new Date().getTime();
            long ttl = endTime - time;//存活时间
            if (time >= startTime && time <= endTime) {
                // 2-2 校验随机码和商品id
                String randomCode = redis.getRandomCode();
                String skuId = redis.getPromotionSessionId() + "_" + redis.getSkuId();
                if (randomCode.equals(key) && killId.equals(skuId)) {
                    // 2-3 校验购物数量是否合理
                    if (num <= redis.getSeckillLimit()) {
                        // 2-4 验证这个人是否已经购买过。
                        // 幂等性；只要秒杀成功就去占位(SETNX)，key使用userId_SessionId_skuId
                        String redisKey = respVo.getId() + "_" + skuId;
                        // 当场秒杀活动结束自动过期
                        Boolean aBoolean = redisTemplate.opsForValue()
                                .setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                        if (aBoolean) {
                            // 占位成功说明从俩没有买过
                            RSemaphore semaphore = redissonClient.
                                    getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                            // 不能用Acquire会一直阻塞
                            boolean b = semaphore.tryAcquire(num);
                            if (b) { // 秒杀成功 快速下单，发送MQ消息
                                String timeId = IdWorker.getTimeId();
                                SeckillOrderTo orderTo = new SeckillOrderTo();
                                orderTo.setOrderSn(timeId);
                                orderTo.setMemberId(respVo.getId());
                                orderTo.setNum(num);
                                orderTo.setPromotionSessionId(redis.getPromotionSessionId());
                                orderTo.setSkuId(redis.getSkuId());
                                orderTo.setSeckillPrice(redis.getSeckillPrice());
                                rabbitTemplate.convertAndSend("order-event-exchange"
                                        , "order.seckill.order", orderTo);
                                return timeId; // 返回订单号
                            } else return null;
                        }
                    } else return null;
                }
            } else return null;
        }

        return null;
}
}
