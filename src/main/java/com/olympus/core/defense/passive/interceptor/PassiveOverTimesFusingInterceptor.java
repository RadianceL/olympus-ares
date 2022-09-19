package com.olympus.core.defense.passive.interceptor;

import cn.hutool.core.date.DateUtil;
import com.olympus.core.defense.support.PassiveOverTimesFusingRole;
import com.olympus.utils.sliding.TimeWindowSliding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 多次访问熔断拦截
 * since 7/25/22
 *
 * @author eddie
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PassiveOverTimesFusingInterceptor implements HandlerInterceptor {

    /**
     * 滑动时间窗
     */
    private final TimeWindowSliding timeWindowSliding;
    /**
     * 超过访问次数规则
     */
    private final PassiveOverTimesFusingRole passiveOverTimesFusingRole;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestKey = passiveOverTimesFusingRole.defineRequestRecordKey(request, response);
        if (timeWindowSliding.allowLimitTimes(requestKey)) {
            log.info("InitiativeOverTimesFusingInterceptor report：this URI [{}]，time:[{}], require [{}]",
                    requestKey, DateUtil.now(),"ok");
            return true;
        }
        // 模糊请求次数
        int vagueRequestTime = timeWindowSliding.allowNotLimitTotal(requestKey);
        log.info("InitiativeOverTimesFusingInterceptor report：this URI [{}]，time:[{}], require [{}] times in current window",
                requestKey, DateUtil.now(), vagueRequestTime);

        return false;
    }

}
