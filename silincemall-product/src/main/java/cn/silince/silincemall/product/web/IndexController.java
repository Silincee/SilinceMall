package cn.silince.silincemall.product.web;

import cn.silince.silincemall.product.entity.CategoryEntity;
import cn.silince.silincemall.product.service.CategoryService;
import cn.silince.silincemall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-18 19:56
 **/
@Controller
public class IndexController {

    @Resource
    private CategoryService categoryService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * @description: 来到首页请求
     */
    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 1. 查出所有一级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        // 视图解析器进行拼串
        // classpath:/templates{}.html
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    /**
     * @description: index/catalog.json
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {

        Map<String, List<Catelog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    /**
     * @description: 压测 简单服务
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {

        return "hello";
    }

    /**
     * @description: Redisson 读写锁测试
     */
    @ResponseBody
    @GetMapping("/write")
    public String writeValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.writeLock();
        String s = "";
        // 改数据加写锁，读数据加读锁
        rLock.lock();
        try {
            s = UUID.randomUUID().toString();
            TimeUnit.SECONDS.sleep(30);
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * @description: Redisson 读写锁测试
     */
    @ResponseBody
    @GetMapping("/read")
    public String readValue() {
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = lock.readLock();
        rLock.lock(); // 加读锁
        try {
            s = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    /**
    * @description: 分布式信号量测试 可以用于分布式限流
    */
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
//        park.acquire(); // 阻塞式等待。获取一个信号,获取一个值/占一个车位
        boolean b = park.tryAcquire(); // 有就抢占，没有就算了,可以用于限流
        if (b){
            // 执行业务
            return "ok";
        }else {
            return "error";
        }

    }

    @ResponseBody
    @GetMapping("/go")
    public String go() {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release(); // 释放一个信号/车位
        return "ok";
    }

    /**
    * @description: 分布式闭锁测试
    */
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await(); // 等待闭锁完成
        return "门已锁好，放假咯...";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Long id) {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown(); // 计数-1
        return id+"班的人都走了";
    }

}
