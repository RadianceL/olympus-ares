package com.olympus.configuration;

import com.olympus.core.defense.interceptor.InitiativeOverTimesFusingInterceptor;
import com.olympus.utils.sliding.TimeWindowSliding;
import com.olympus.utils.sliding.data.TimeWindowSlidingDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 阿瑞斯战神防御拦截器自动配置
 * since 7/25/22
 *
 * @author eddie
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AresDefenseWebConfigure implements WebMvcConfigurer {

    /**
     * 滑动时间窗
     */
    private final AresDefenseSpringConfiguration aresDefenseSpringConfiguration;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        TimeWindowSliding timeWindowSliding = new TimeWindowSliding(TimeWindowSlidingDataSource.defaultDataSource(), 10, 1000, 1);
        InitiativeOverTimesFusingInterceptor interceptor = new InitiativeOverTimesFusingInterceptor(timeWindowSliding);
        registry.addInterceptor(interceptor).addPathPatterns("/**");
    }
}
