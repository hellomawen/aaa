package com.sxt.filter;

import com.sxt.pojo.User;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MyFormAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        //将保存到Session的 SaveAndRequest数据清除掉
        WebUtils.getAndClearSavedRequest(request);
        return super.onLoginSuccess(token, subject, request, response);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
    {
        //从请求中获取Shiro的 主体
        Subject subject = getSubject(request, response);
        //从主体中获取Shiro框架的Session
        Session session = subject.getSession();
        //如果主体没有认证（Session中认证）并且 主体已经设置记住我了
        if (!subject.isAuthenticated() && subject.isRemembered())
        {
            //获取主体的身份（从记住我的Cookie中获取的）
            User principal = (User) subject.getPrincipal();
            //将身份认证信息共享到 Session中
            session.setAttribute("admin", principal);
        }
        return subject.isAuthenticated() || subject.isRemembered();
    }
}
