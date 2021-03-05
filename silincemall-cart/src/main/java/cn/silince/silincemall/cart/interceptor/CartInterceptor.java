package cn.silince.silincemall.cart.interceptor;

import cn.silince.common.constant.AuthServerConstant;
import cn.silince.common.constant.CartConstant;
import cn.silince.common.vo.MemberRespVo;
import cn.silince.silincemall.cart.to.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @program: SilinceMall
 * @description: 在执行目标方法之前，判断用户的登陆状态,并且封装传递给controller目标请求
 * @author: Silince
 * @create: 2021-02-26 00:10
 **/
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
    * @description: 在目标方法执行之前拦截
    * @param: [request, response, handler]
    * @return: boolean
    * @author: Silince
    * @date: 2/26/21
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo member=(MemberRespVo)session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (member!=null){
             // 用户已经登陆
            userInfoTo.setUserId(member.getId());
        }


        // cookie中存在则是用户第一次使用jd购物车功能，需要给一个临时的用户身份
        // user-key标识用户身份，一个月后过期
        Cookie[] cookies = request.getCookies();
        if (cookies!=null&&cookies.length>0){
            for (Cookie cookie : cookies) {
                // user-key
                if (cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setTempUser(true); // 找到了该cookie 说明存在临时用户
                    userInfoTo.setUserKey(cookie.getValue());
                }
            }
        }

        // 如果没有临时用户一定要分配一个临时用户
        if (StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }

        // 在目标方法(CartController::cartListPage)执行之前 放入userInfoTo数据
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
    * @description: 分配一个临时用户(业务执行之后让浏览器保存 cookie-->user-key用户身份标识)
    */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();

        // 判断是否需要分配临时用户
        if (!userInfoTo.isTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("silincemall.com"); // 作用域
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT); // 一个月之后过期
            response.addCookie(cookie);
        }
    }
}
