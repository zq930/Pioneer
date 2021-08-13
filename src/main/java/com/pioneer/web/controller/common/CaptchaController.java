package com.pioneer.web.controller.common;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import com.pioneer.common.constant.Constants;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.redis.RedisCache;
import com.pioneer.web.system.service.ISysConfigService;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author hlm
 * @date 2021-08-09 17:51:05
 */
@RestController
public class CaptchaController {

    @Resource
    private RedisCache redisCache;

    @Resource
    private ISysConfigService configService;

    /**
     * 生成验证码
     *
     * @return 结果
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCode() {
        AjaxResult ajax = AjaxResult.success();
        // 查询参数设置：是否开启验证码
        boolean captchaOnOff = configService.selectCaptchaOnOff();
        ajax.put("captchaOnOff", captchaOnOff);
        if (!captchaOnOff) {
            return ajax;
        }
        // 校验键值
        String uuid = IdUtil.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        // 生成验证码
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(160, 60, 4, 100);
        // 放入缓存：默认2分钟有效
        redisCache.setCacheObject(verifyKey, captcha.getCode(), Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换字节流输出
        FastByteArrayOutputStream sos = new FastByteArrayOutputStream();
        captcha.write(sos);
        // 返回信息
        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(sos.toByteArray()));
        return ajax;
    }
}
