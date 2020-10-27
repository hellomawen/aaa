package com.sxt.realm;

import com.sxt.pojo.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MyRealm extends AuthorizingRealm {
    @Override
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    // 取出以认证的用户信息
    User user = (User) principals.getPrimaryPrincipal();
    System.out.println(user);

    // 模拟从数据库中查询权限
    Set<String> prms = new TreeSet<>();
    prms.add("user:list");

    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
    info.setStringPermissions(prms);

    return info;
}

    /**
     * 认证
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 造一个假的对象（模拟从数据库中取出）
        User user = new User();
        user.setName("user");
        user.setPassword("d0e7877cb0e6b3a81a1a5fa93960f65e");
        user.setSalt("rrrr");

        ByteSource salt = ByteSource.Util.bytes(user.getSalt());
        return new SimpleAuthenticationInfo(user, user.getPassword(),salt,this.getName());



    }
}
