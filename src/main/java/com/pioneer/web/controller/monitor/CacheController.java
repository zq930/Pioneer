package com.pioneer.web.controller.monitor;

import cn.hutool.core.util.StrUtil;
import com.pioneer.common.core.domain.AjaxResult;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 缓存监控
 *
 * @author hlm
 * @date 2021-08-09 17:51:19
 */
@RestController
@RequestMapping("/monitor/cache")
public class CacheController {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取缓存监控信息
     *
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('monitor:cache:list')")
    @GetMapping()
    public AjaxResult getInfo() {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::info);
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        if (commandStats != null) {
            commandStats.stringPropertyNames().forEach(key -> {
                Map<String, String> data = new HashMap<>(4);
                String property = commandStats.getProperty(key);
                data.put("name", StrUtil.removePrefix(key, "cmdstat_"));
                data.put("value", StrUtil.subBetween(property, "calls=", ",usec"));
                pieList.add(data);
            });
        }

        // 返回
        Map<String, Object> result = new HashMap<>(8);
        result.put("info", info);
        result.put("dbSize", dbSize);
        result.put("commandStats", pieList);
        return AjaxResult.success(result);
    }
}
