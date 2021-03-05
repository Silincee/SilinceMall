package cn.silince.silincemall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:03:11
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /** 
    * @description: 根据会员id返回所有收货地址列表 
    */ 
    List<MemberReceiveAddressEntity> getAddress(Long memberId);
}

