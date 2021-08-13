package com.pioneer.web.controller.monitor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.pioneer.common.annotation.Log;
import com.pioneer.common.constant.Constants;
import com.pioneer.common.core.controller.BaseController;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.core.domain.LoginUser;
import com.pioneer.common.core.redis.RedisCache;
import com.pioneer.common.enums.BusinessType;
import com.pioneer.web.system.domain.SysUserOnline;
import com.pioneer.web.system.service.ISysUserOnlineService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 *
 * @author hlm
 * @date 2021-08-09 17:51:46
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {

    @Resource
    private ISysUserOnlineService userOnlineService;

    @Resource
    private RedisCache redisCache;

    /**
     * 获取在线用户列表
     *
     * @param ipaddr   登录ip
     * @param userName 用户名
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('monitor:online:list')")
    @GetMapping("/list")
    public AjaxResult list(String ipaddr, String userName) {
        Collection<String> keys = redisCache.keys(Constants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        for (String key : keys) {
            LoginUser user = redisCache.getCacheObject(key);
            if (StrUtil.isNotEmpty(ipaddr) && StrUtil.isNotEmpty(userName)) {
                if (StrUtil.equals(ipaddr, user.getIpaddr()) && StrUtil.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                }
            } else if (StrUtil.isNotEmpty(ipaddr)) {
                if (StrUtil.equals(ipaddr, user.getIpaddr())) {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                }
            } else if (StrUtil.isNotEmpty(userName) && ObjectUtil.isNotNull(user.getUser())) {
                if (StrUtil.equals(userName, user.getUsername())) {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                }
            } else {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }

    /**
     * 强退用户
     *
     * @param tokenId tokenId
     * @return 结果
     */
    @PreAuthorize("@ss.hasPerm('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public AjaxResult forceLogout(@PathVariable String tokenId) {
        redisCache.deleteObject(Constants.LOGIN_TOKEN_KEY + tokenId);
        return AjaxResult.success();
    }
}
