package cn.silince.silincemall.seckill.scheduled;

import cn.silince.silincemall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @program: SilinceMall
 * @description: 秒杀商品定时上架
 * @author: Silince
 * @create: 2021-03-03 17:02
 **/
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Resource
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    // 分布式锁。 定时任务上架
    private final String UPLOAD_LOCK = "seckill:upload:lock";


    /**
     * @description: 每天晚上3点；上架最近三天需要秒杀的商品。
     *                  当天00:00:00  - 23:59:59
     *                  明天00:00:00  - 23:59:59
     *                  后天00:00:00  - 23:59:59
     */
    // TODO 幂等性处理 上架过不用再上架了
//    @Scheduled(cron = "0 0 3 * * ?")
    @Scheduled(cron = "0 * * * * ?")
    public void uploadSeckillSkuLatest3Days(){
        // 重复上架无需处理
        log.info("上架秒杀的商品信息...");
        // 分布式锁
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            seckillService.uploadSeckullSkuLatest3Days();
        } finally {
            lock.unlock();
        }
    }
}
