package cn.silince.silincemall.ware.service.impl;

import cn.silince.common.utils.R;
import cn.silince.silincemall.ware.feign.MemberFeignService;
import cn.silince.silincemall.ware.vo.FareVo;
import cn.silince.silincemall.ware.vo.MemberAddressVo;
import cn.silince.silincemall.ware.vo.SkuHasStockVo;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.silince.common.utils.PageUtils;
import cn.silince.common.utils.Query;

import cn.silince.silincemall.ware.dao.WareInfoDao;
import cn.silince.silincemall.ware.entity.WareInfoEntity;
import cn.silince.silincemall.ware.service.WareInfoService;

import javax.annotation.Resource;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Resource
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            queryWrapper.eq("id",key).or().like("name",key)
                    .or().like("address",key)
                    .or().like("areacode",key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeignService.addrInfo(addrId);
        MemberAddressVo data = r.getData("memberReceiveAddress",new TypeReference<MemberAddressVo>() {
        });
        if(data!=null){
            // 需要调用第三方接口计算运费 现在只是模拟一下
            String phone = data.getPhone();
            // 截取手机号的最后一位作为运费
            String substring = phone.substring(phone.length() - 1, phone.length());
            BigDecimal fare = new BigDecimal(substring);
            fareVo.setAddress(data);
            fareVo.setFare(fare);
            return fareVo;
        }
        return null;
    }
}