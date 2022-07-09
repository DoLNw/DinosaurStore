package com.jcwang.store.order.interceptor;

import com.jcwang.store.constant.AuthServerConstant;
import com.jcwang.store.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/order/order/status/**", uri);
        if (match) {
            return true;
        }

        MemberResponseVo attribute = (MemberResponseVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);

        if (attribute != null) {
            loginUser.set(attribute);

        } else {
            // 没登陆就先去登陆
            request.getSession().setAttribute("msg", "请先进行登录");
            response.sendRedirect("http://auth.dinosaurstore.com/login.html");
            return false;
        }


        return true;
    }
}
