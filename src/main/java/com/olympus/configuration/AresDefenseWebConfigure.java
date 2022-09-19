package com.olympus.configuration;

import com.olympus.core.defense.passive.interceptor.PassiveOverTimesFusingInterceptor;
import com.olympus.core.defense.support.PassiveOverTimesFusingRole;
import com.olympus.utils.sliding.TimeWindowSliding;
import com.olympus.utils.sliding.data.TimeWindowSlidingDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

/**
 * 阿瑞斯战神防御拦截器自动配置
 * since 7/25/22
 *
 * @author eddie
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AresDefenseWebConfigure implements WebMvcConfigurer {

    /**
     * 滑动时间窗
     */
    private final AresDefenseConfiguration aresDefenseConfiguration;
    /**
     * 超过访问次数规则
     */
    private final PassiveOverTimesFusingRole passiveOverTimesFusingRole;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AresDefenseConfiguration.PassiveDefenseConfig passiveDefenseConfig = aresDefenseConfiguration.getPassiveDefenseConfig();
        if (Objects.isNull(passiveDefenseConfig)) {
            throw new IllegalArgumentException("initialization PassiveOverTimesFusingInterceptor error，parameter olympus.ares.passive-defense-config missing");
        }
        AresDefenseConfiguration.OverTimesFusingInterceptorConfig overTimesFusingInterceptorConfig = passiveDefenseConfig.getOverTimesFusingInterceptorConfig();
        if (Objects.isNull(overTimesFusingInterceptorConfig)) {
            throw new IllegalArgumentException("initialization PassiveOverTimesFusingInterceptor error，parameter olympus.ares.passiveDefenseConfig.over-times-fusing-interceptor-config missing");
        }

        TimeWindowSliding timeWindowSliding = new TimeWindowSliding(TimeWindowSlidingDataSource.defaultDataSource(),
                overTimesFusingInterceptorConfig.getWindowSize(), overTimesFusingInterceptorConfig.getTimeMillisPerSlice(),
                overTimesFusingInterceptorConfig.getThreshold());

        PassiveOverTimesFusingInterceptor interceptor = new PassiveOverTimesFusingInterceptor(timeWindowSliding, passiveOverTimesFusingRole);
        registry.addInterceptor(interceptor).addPathPatterns("/**");
        log.info("olympus-ares: initialize PassiveOverTimesFusingInterceptor success");
    }
}
