package cn.silince.silincemall.member.service;

import cn.silince.silincemall.member.exception.PhoneExistException;
import cn.silince.silincemall.member.exception.UsernameExistException;
import cn.silince.silincemall.member.vo.MemberLoginVo;
import cn.silince.silincemall.member.vo.MemberRegistVo;
import cn.silince.silincemall.member.vo.SocialUserVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:03:11
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /** 
    * @description: 新会员的注册 
    */ 
    void regist(MemberRegistVo vo);

    /**
    * @description: 检查手机号是否唯一
    */
    void checkPhoneUnique(String Phone) throws PhoneExistException;

    /**
     * @description: 检查用户名是否唯一
     */
    void checkUsernameUnique(String username) throws UsernameExistException;

    /**
    * @description: 会员的登陆--普通登陆
    */
    MemberEntity login(MemberLoginVo vo);

    /**
     * @description: 会员的登陆--社交登陆
     */
    MemberEntity login(SocialUserVo vo) throws Exception;
}

