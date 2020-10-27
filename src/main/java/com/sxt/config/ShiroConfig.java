package com.sxt.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.sxt.filter.MyFormAuthenticationFilter;
import com.sxt.realm.MyRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * shiro 配置类
 *
 * @author 86185
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

//    //设置Shiro框架对注解支持
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
//        return authorizationAttributeSourceAdvisor;
//    }
//
//    @Bean
//    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        /*设置可以让Shiro框架使用AOP为表现层创建代理（Shiro权限判断的注解全部在表现层）*/
//        autoProxyCreator.setProxyTargetClass(true);
//        return autoProxyCreator;
//    }

    //创建对SpringMVC抛出异常处理解析器
    @Bean
    public SimpleMappingExceptionResolver simpleMappingExceptionResolver(){
        Properties properties = new Properties();
        properties.put("org.apache.shiro.authz.UnauthorizedException","/unauthorized");
        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
        simpleMappingExceptionResolver.setExceptionMappings(properties);

        return simpleMappingExceptionResolver;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        // 配置安全管理器
        factoryBean.setSecurityManager(securityManager());

        // 配置认证失败以后跳转页面,一般是跳转到登录页面（必须经控制器转发）
        factoryBean.setLoginUrl("/admin/loginError");

        // 配置认证成功后跳转页面
        factoryBean.setSuccessUrl("/index");

        // 配置没有权限跳转的页面
        factoryBean.setUnauthorizedUrl("unauthorized.html");

        // 替换过滤器为自定义过滤器
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("authc",myFormAuthenticationFilter());
        factoryBean.setFilters(filterMap);

        /**
         * 配置过滤器链，用于针对不同的资源过滤不同的请求
         * shiro内置了针对不同资源的多种过滤器规则
         * 过滤器从上到下执行，一次放行后不会再执行后面的过滤器
         */

        // 创建过滤器链集合
        Map<String, String> chainMap = new HashMap<>();
        // 匿名过滤器,不需要认证和权限直接放行,anon:匿名过滤器的别名
        chainMap.put("/js/**", "anon");
        chainMap.put("/css/**", "anon");
        chainMap.put("/images/**", "anon");
        chainMap.put("/login.html", "anon");
        // 退出过滤器（退出登陆时自动清除session）
        chainMap.put("/logout", "logout");

        // 认证过滤器，必须认证才放行否则跳转到认证失败页面
        chainMap.put("/**", "authc");

        factoryBean.setFilterChainDefinitionMap(chainMap);

        return factoryBean;
    }

    public MyFormAuthenticationFilter myFormAuthenticationFilter(){
        return new MyFormAuthenticationFilter();
    }

    // 安全管理器
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 注入自定义 realm
        securityManager.setRealm(myRealm());
        // 注入缓存管理器
        securityManager.setCacheManager(cacheManager());
        // 注入会话管理器
        securityManager.setSessionManager(sessionManager());
        // 注入记住我管理器
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    @Bean
    public RememberMeManager rememberMeManager(){
        CookieRememberMeManager manager = new CookieRememberMeManager();
        manager.setCookie(cookie());
        // https://www.cnblogs.com/kkxwze/p/13355632.html
        manager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
        return manager;
    }

    @Bean
    public Cookie cookie(){
        SimpleCookie simpleCookie = new SimpleCookie();
        // 设置 cookie 最大存活时间，单位：秒
        simpleCookie.setMaxAge(60 * 60 * 24 * 7);
        simpleCookie.setName("RememberMe");
        return simpleCookie;
    }

    /*配置会话管理器*/
    @Bean
    public SessionManager sessionManager(){
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        //设置超时时间，单位：milliseconds
        defaultWebSessionManager.setGlobalSessionTimeout(1000*100);

        return  defaultWebSessionManager;
    }


    /*配置缓存管理器*/
    @Bean
    public CacheManager cacheManager(){
        EhCacheManager ehCacheManager = new EhCacheManager();
        //设置自定义缓存策略的配置文件
        //ehCacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
        return  ehCacheManager;
    }


    // 自定义 realm
    @Bean
    public MyRealm myRealm() {
        MyRealm realm = new MyRealm();
        realm.setCredentialsMatcher(myMatcher());
        return realm;

    }

    // 配置凭证匹配器
    @Bean
    public HashedCredentialsMatcher myMatcher(){
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");
        matcher.setHashIterations(3);
        return matcher;
    }
}
