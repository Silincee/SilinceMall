package cn.silince.silincemall.auth.feign;

import cn.silince.common.utils.R;
import cn.silince.silincemall.auth.vo.SocialUserVo;
import cn.silince.silincemall.auth.vo.UserLoginVo;
import cn.silince.silincemall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: SilinceMall
 * @description: 用户模块远程接口
 * @author: Silince
 * @create: 2021-02-24 22:45
 **/
@FeignClient("silincemall-member")
public interface MemberFeignService {

    /**
     * @description: 会员的注册
     */
    @PostMapping("/member/member/regist")
    public R regist(@RequestBody UserRegistVo vo);

    /**
     * @description: 会员登录
     */
    @PostMapping("/member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    /**
     * @description: 会员登录--社交登陆
     */
    @PostMapping("/member/member/oauth2/login")
    public R oauth2Login(@RequestBody SocialUserVo vo) throws Exception;
}
