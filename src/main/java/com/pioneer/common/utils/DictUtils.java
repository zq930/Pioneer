package com.pioneer.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.pioneer.common.constant.Constants;
import com.pioneer.common.core.redis.RedisCache;
import com.pioneer.web.system.domain.SysDictData;

import java.util.Collection;
import java.util.List;

/**
 * 字典工具类
 *
 * @author hlm
 * @date 2021-08-09 17:46:10
 */
public class DictUtils {

    /**
     * 设置字典缓存
     *
     * @param key  参数键
     * @param list 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> list) {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), list);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return list 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key) {
        Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (ObjectUtil.isNotNull(cacheObj)) {
            return Convert.toList(SysDictData.class, cacheObj);
        }
        return null;
    }

    /**
     * 删除指定字典缓存
     *
     * @param key 字典键
     */
    public static void removeDictCache(String key) {
        SpringUtils.getBean(RedisCache.class).deleteObject(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(Constants.SYS_DICT_KEY + "*");
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return Constants.SYS_DICT_KEY + configKey;
    }
}
