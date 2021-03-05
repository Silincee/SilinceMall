package cn.silince.silincemall.auth.controller;

import cn.silince.common.utils.HttpUtils;
import cn.silince.common.utils.R;
import cn.silince.silincemall.auth.feign.MemberFeignService;
import cn.silince.common.vo.MemberRespVo;
import cn.silince.silincemall.auth.vo.SocialUserVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: SilinceMall
 * @description: 处理社交登陆请求
 * @author: Silince
 * @create: 2021-02-25 10:58
 **/
@Controller
@Slf4j
public class OAuth2Controller {

    @Resource
    private MemberFeignService memberFeignService;

    /**
    * @description: 处理微博返回code码
    */
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {
        Map<String,String> header = new HashMap<>();
        Map<String,String> query = new HashMap<>();

        HashMap<String, String> map = new HashMap<>();
        // 1 根据code换取accessToken
        map.put("client_id","1387651299");
        map.put("client_secret","df9e412448c90be10b4614752e77cb5b");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.silincemall.com/oauth2.0/weibo/success");
        map.put("code",code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token",
                "post", header, query, map);

        // 2 处理
        if (response.getStatusLine().getStatusCode()==200){
            // 获取到了 accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUserVo socialUser = JSON.parseObject(json, SocialUserVo.class);
            // 知道了是哪个weibo用户
            // 2-1 当前用户是如果是第一次进入网站，自动注册来的(为当前社交用户生成一个会员信息账号，以后这个社交账号就对应指定的会员)
            // 远程调用服务 登陆或者注册？
            R oauth2Login = memberFeignService.oauth2Login(socialUser);
            if (oauth2Login.getCode()==0){
                MemberRespVo data = oauth2Login.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登陆成功,用户信息: {}",data.toString());
                // 3 登陆成功就跳回首页
                // 第一次使用session：命令浏览器保存 JSESSIONID这个 cookie
                // 以后浏览器访问哪个网站就会带上这个网站的cookie
                // 为了子域之间：silincemall.com auth.silincemall.com ...也能共享
                // 需要指定保存的时候指定域名为父域名，即使是子域名的JSESSIONID cookie也能让父域名直接使用
                // TODO 1 默认发的令牌 session=xxx. 作用域是当前域(解决子域session共享问题)
                // TODO 2 使用JSON序列化的方式来序列化对象到redis，就可以不实现Serializable接口
                session.setAttribute("loginUser",data);
                return "redirect:http://silincemall.com";
            }else {
                return "redirect:http://auth.silincemall.com/login.html";
            }

        }else {
            return "redirect:http://auth.silincemall.com/login.html";
        }


    }
}
