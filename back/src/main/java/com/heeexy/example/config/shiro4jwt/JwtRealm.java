package com.heeexy.example.config.shiro4jwt;

import com.alibaba.fastjson.JSONObject;
import com.heeexy.example.config.jwt.JwtUtil;
import com.heeexy.example.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class JwtRealm extends AuthorizingRealm {

    @Autowired
    private LoginService loginService;

    /*
     * 多重写一个support
     * 标识这个Realm是专门用来验证JwtToken
     * 不负责验证其他的token（UsernamePasswordToken）
     * */
    @Override
    public boolean supports(AuthenticationToken token) {
        //这个token就是从过滤器中传入的jwtToken
        return token instanceof JwtToken;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    //认证
    //这个token就是从过滤器中传入的jwtToken
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String jwt = (String) token.getPrincipal();
        if (jwt == null) {
            throw new NullPointerException("jwtToken 不允许为空");
        }
        //判断
        JwtUtil jwtUtil = new JwtUtil();
        if (!jwtUtil.isVerify(jwt)) {  // 在这里 做了Token 的验证，
                                       // Token 验证的标准已经不是去比对密码，而是看是否过期 和 头部是否被篡改
            throw new UnknownAccountException();
        }


        //下面是验证这个user是否是真实存在的
        String username = (String) jwtUtil.decode(jwt).get("username"); //判断数据库中username是否存在
/*        String password = (String) jwtUtil.decode(jwt).get("password"); //判断数据库中username是否存在

        // 从Token 中拿到了用username  , 但是拿不到密码
        // 去数据库验证一次 ，如果找不到对象说明对象不存在或者密码错误
        JSONObject user = loginService.getUser(username,password);
        if(user==null) {
            throw new UnknownAccountException();
        }*/

        log.info("在使用token登录"+username);
        return new SimpleAuthenticationInfo(jwt,jwt,"JwtRealm");
        //这里返回的是类似账号密码的东西，但是jwtToken都是jwt字符串。还需要一个该Realm的类名
    }

}
