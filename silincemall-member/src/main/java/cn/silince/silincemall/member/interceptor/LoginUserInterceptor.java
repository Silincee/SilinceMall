package cn.silince.silincemall.member.interceptor;

import cn.silince.common.constant.AuthServerConstant;
import cn.silince.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: SilinceMall
 * @description: 用户登录拦截器
 * @author: Silince
 * @create: 2021-02-27 16:46
 **/
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    /** 
    * @description: 判断是否登陆 
    */ 
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 如果是解锁库存的请求(消费mq的时候) 无需登陆 直接放行即可
        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if (match) return true;


        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
            if (attribute!=null){
                loginUser.set(attribute);
                return true;
            }else {
                // 没登陆就去登陆
                request.getSession().setAttribute("msg","请先进行登陆");
                response.sendRedirect("http://auth.silincemall.com/login.html");
                return false;
            }
        }
}
