package cn.silince.silincemall.product;

import cn.silince.silincemall.product.dao.AttrGroupDao;
import cn.silince.silincemall.product.dao.SkuSaleAttrValueDao;
import cn.silince.silincemall.product.entity.BrandEntity;
import cn.silince.silincemall.product.service.BrandService;
import cn.silince.silincemall.product.vo.SkuItemSaleAttrVo;
import cn.silince.silincemall.product.vo.SkuItemVo;
import cn.silince.silincemall.product.vo.SpuItemAttrGroupVo;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SilincemallProductApplication.class})
public class SilincemallProductApplicationTests {

    @Autowired
    BrandService brandService;

//    @Resource
//    OSSClient ossClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Test
    public void testRedisson(){
        System.out.println(redissonClient);
    }

    @Test
    public void stringRedisTemplate() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 保存
        ops.set("hello","world_"+ UUID.randomUUID().toString());
        // 查询
        String hello = ops.get("hello");
        System.out.println("之前保存的数据是: "+hello);

    }

    @Test
    public void contextLoads() {

        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");

//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("华为");
//        brandService.updateById(brandEntity);

//        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
//        list.forEach(System.out::println);
    }

    @Test
    public void Test(){
//        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(7L, 225L);
//        System.out.println(attrGroupWithAttrsBySpuId.toString());

        List<SkuItemSaleAttrVo> saleAttrBySpuId = skuSaleAttrValueDao.getSaleAttrBySpuId(7L);
        System.out.println(saleAttrBySpuId.toString());

    }

    /**
     * @description: aliyun oss 文件上传测试
     */
//    @Test
//    public void uploadFile() throws FileNotFoundException {
//        // Endpoint以杭州为例，其它Region请按实际情况填写。
//        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
//        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，
//        // 创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
//        String accessKeyId = "LTAI4FyQDSt1MmZwnthHTPPs";
//        String accessKeySecret = "jPAQnMs76taIXIRNqoYWBFtdajzWB1";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        // 上传文件流。
//        InputStream inputStream = new FileInputStream("/Users/silince/Develop/MagicDontTouch/IdeaProjects/Silince电商项目/SilinceMall/silincemall-product/src/main/resources/static/QQ20190925-144616.png");
//        ossClient.putObject("silincemall-hello", "QQ20190925-144616.png", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
//        System.out.println("文件上传完成");
//    }

    /**
     * @description: SpringCloud Alibaba-OSS 文件上传测试
     */
//    @Test
//    public void uploadFileBySpringCloudAliOSS() throws FileNotFoundException {
//
//        // 上传文件流
//        FileInputStream inputStream = new FileInputStream("/Users/silince/Develop/MagicDontTouch/IdeaProjects/Silince电商项目/SilinceMall/silincemall-product/src/main/resources/static/WechatIMG330.jpeg");
//
//        ossClient.putObject("silincemall-hello","WechatIMG330.jpeg",inputStream);
//
//        // 关闭ossclient
//        ossClient.shutdown();
//        System.out.println("文件上传完成");
//    }

}
