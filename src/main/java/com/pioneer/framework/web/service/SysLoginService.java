package com.pioneer.framework.web.service;

import cn.hutool.core.util.StrUtil;
import com.pioneer.common.constant.Constants;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.common.core.redis.RedisCache;
import com.pioneer.common.exception.CustomException;
import com.pioneer.common.utils.ServletUtils;
import com.pioneer.framework.manager.AsyncManager;
import com.pioneer.framework.manager.factory.AsyncFactory;
import com.pioneer.web.system.domain.SysUser;
import com.pioneer.web.system.service.ISysConfigService;
import com.pioneer.web.system.service.ISysUserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 登录校验方法
 *
 * @author hlm
 * @date 2021-08-09 17:50:13
 */
@Component
public class SysLoginService {

    @Resource
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private RedisCache redisCache;

    @Resource
    private ISysUserService userService;

    @Resource
    private ISysConfigService configService;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(username, code, uuid);
        }
        // 用户验证
        Authentication authentication;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            String msg;
            if (e instanceof BadCredentialsException) {
                msg = "用户名或密码错误";
            } else {
                msg = e.getMessage();
            }
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, msg));
            throw new CustomException(msg);
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登陆成功"));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(String username, String code, String uuid) {
        if (uuid == null) {
            uuid = StrUtil.EMPTY;
        }
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            String msg = "验证码已失效";
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, msg));
            throw new CustomException(msg);
        }
        if (!code.equalsIgnoreCase(captcha)) {
            String msg = "验证码错误";
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, msg));
            throw new CustomException(msg);
        }
    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(ServletUtils.getClientIP(ServletUtils.getRequest()));
        sysUser.setLoginDate(LocalDateTime.now());
        userService.updateUserProfile(sysUser);
    }
}
