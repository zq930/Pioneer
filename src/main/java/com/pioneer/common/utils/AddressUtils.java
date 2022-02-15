package com.pioneer.common.utils;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 查询地址工具类
 *
 * @author hlm
 * @date 2021-08-09 17:46:04
 */
@Slf4j
public class AddressUtils {

    public static final String IP_URL = "https://whois.pconline.com.cn/ipJson.jsp?json=true&ip=";

    public static final String UNKNOWN = "未知";

    public static String getAddressByIp(String ip) {
        // 判断是否IPV4
        if (Validator.isIpv4(ip)) {
            // 判断内外网
            if (NetUtil.isInnerIP(ip)) {
                return "内网IP";
            }
        } else {
            // 判断是否IPV6
            if (!Validator.isIpv6(ip)) {
                // 未知类型
                return UNKNOWN;
            }
        }
        try {
            // 根据IP查询实际地理位置
            String rspStr = HttpUtil.get(IP_URL + ip);
            if (StrUtil.isEmpty(rspStr)) {
                log.error("获取地理位置异常 {}", ip);
                return UNKNOWN;
            }
            JSONObject obj = JSONUtil.parseObj(rspStr);
            String pro = obj.getStr("pro");
            String city = obj.getStr("city");
            return String.format("%s %s", pro, city);
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", ip);
        }
        return UNKNOWN;
    }
}
