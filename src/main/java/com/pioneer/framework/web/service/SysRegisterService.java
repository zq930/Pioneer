package com.pioneer.framework.web.service;

import cn.hutool.core.util.StrUtil;
import com.pioneer.common.constant.Constants;
import com.pioneer.common.constant.UserConstants;
import com.pioneer.common.core.domain.RegisterBody;
import com.pioneer.common.core.redis.RedisCache;
import com.pioneer.common.exception.CustomException;
import com.pioneer.common.utils.SecurityUtils;
import com.pioneer.framework.manager.AsyncManager;
import com.pioneer.framework.manager.factory.AsyncFactory;
import com.pioneer.web.system.domain.SysUser;
import com.pioneer.web.system.service.ISysConfigService;
import com.pioneer.web.system.service.ISysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 注册校验方法
 *
 * @author hlm
 * @date 2021-08-09 17:50:27
 */
@Component
public class SysRegisterService {

    @Resource
    private ISysUserService userService;

    @Resource
    private ISysConfigService configService;

    @Resource
    private RedisCache redisCache;

    /**
     * 注册
     */
    public String register(RegisterBody registerBody) {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff) {
            validateCaptcha(registerBody.getCode(), registerBody.getUuid());
        }
        if (StrUtil.isEmpty(username)) {
            msg = "用户名不能为空";
        } else if (StrUtil.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(username))) {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        } else {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(username);
            sysUser.setNickName(username);
            sysUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, "注册成功"));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     *
     * @param code 验证码
     * @param uuid 唯一标识
     */
    public void validateCaptcha(String code, String uuid) {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {
            throw new CustomException("验证码已失效");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CustomException("验证码错误");
        }
    }
}
