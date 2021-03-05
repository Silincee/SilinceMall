package cn.silince.silincemall.auth.controller;

import cn.silince.common.constant.AuthServerConstant;
import cn.silince.common.exception.BizCodeEnume;
import cn.silince.common.utils.R;
import cn.silince.common.vo.MemberRespVo;
import cn.silince.silincemall.auth.feign.MemberFeignService;
import cn.silince.silincemall.auth.feign.ThirdPartFeignService;
import cn.silince.silincemall.auth.vo.UserLoginVo;
import cn.silince.silincemall.auth.vo.UserRegistVo;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: SilinceMall
 * @description: 登陆
 * @author: Silince
 * @create: 2021-02-24 17:07
 **/
@Slf4j
@Controller
public class LoginController {

    @Resource
    private ThirdPartFeignService thirdPartFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private MemberFeignService memberFeignService;

    /**
    * @description: 发送一个请求直接跳转到一个页面
     * SpringMVC viewcontroller : 将请求和页面映射过来
    */


    /**
    * @description: 获取短信验证码
    */
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        // 防止同一个phone在60s内再次发送验证码
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis()-l<60000){
                // 60s 内不能再次发送
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(),BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        // TODO 1 验证码接口防刷

        // 2 验证码的再次校验 redis存key-phone  sms:code:18106524119 --> code
        String code = UUID.randomUUID().toString().substring(0, 5);
        log.error("发送的验证码是： "+code);
        String substring = code+"_"+System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,substring,
                10, TimeUnit.MINUTES);

        thirdPartFeignService.sendCode(phone,code);
        return R.ok();
    }


    /**
    * @description: 用户注册
     *  RedirectAttributes 模拟重定向携带数据
     *  重定向携带数据利用的是session原理，将数据放在session中。 只要跳到下一个页面取出这个数据以后，session里面的数据就会删掉
     *  TODO  分布式下的session问题要怎么办？
    */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes){
        System.out.println("用户注册。。。。。。。。。");
        if (result.hasErrors()){
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);

            // Request method 'POST' not supported ？？？🤔
            // 用户注册 --> /regist[post] --> 转发/reg.html(路径映射默认都是get方式访问的)

            // 校验出错，转发到注册页
            //return "forward:/reg.html";
            System.out.println("校验出错，转发到注册页");
            return "redirect:http://auth.silincemall.com/reg.html";
        }
        // 真正注册，调用远程会员服务进行。首先要校验验证码
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)){
            if (code.equals(s.split("_")[0])){
                // 删除验证码 令牌机制(用后即删)
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());

                //  验证码对比成功 真正注册，调用远程会员服务进行
                R r = memberFeignService.regist(vo);
                if (r.getCode()==0){
                    // 远程调用注册成功 回到登陆页
                    return "redirect:http://auth.silincemall.com/login.html";
                }else {
                    // 远程调用注册成功 失败
                    HashMap<String, String> errors = new HashMap<>();
                    errors.put("msg",r.getData(new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.silincemall.com/reg.html";
                }

            }else {
                // 校验失败
                HashMap<String, String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.silincemall.com/reg.html";
            }
        }else {
            // redis没有取到数据 --> 验证码错误
            HashMap<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.silincemall.com/reg.html";
        }


    }

    /**
    * @description: 用户登陆
    */
    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session){

        // 远程调用会员服务进行判断 用户名密码
        R r = memberFeignService.login(vo);
        if (r.getCode()==0){
            // 登陆成功 重定向到首页(并把返回的数据放到session中)
            MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            return "redirect:http://silincemall.com";
        }else {
            // 错误消息
            HashMap<String, String> errors = new HashMap<>();
            errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.silincemall.com/login.html";
        }


    }

    /**
    * @description: 首页判断是否已经登陆过
    */
    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute==null){
            // 没登陆 展示登陆页
            return "login";
        }else {
            // 已经登陆过，直接去商场首页
            return "redirect:http://silincemall.com";
        }
    }
}
