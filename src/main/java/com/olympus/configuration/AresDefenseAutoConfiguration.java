package com.olympus.configuration;

import com.olympus.core.defense.passive.filter.PassiveXssFilter;
import com.olympus.core.defense.support.PassiveOverTimesFusingRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿瑞斯战神防御体系自动配置
 * since 7/25/22
 *
 * @author eddie
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AresDefenseAutoConfiguration {
    /**
     * 滑动时间窗
     */
    private final AresDefenseConfiguration aresDefenseSpringConfiguration;
    /**
     * 超过访问次数规则
     */
    private final PassiveOverTimesFusingRole passiveOverTimesFusingRole;

    @Bean
    @ConditionalOnProperty(prefix = "olympus.ares.passive-defense-config", name = "xss-defense", havingValue = "true")
    public FilterRegistrationBean<PassiveXssFilter> registrationInitiativeXssFilter(){
        log.info("olympus-ares: initialize PassiveXssFilter...");
        //通过FilterRegistrationBean实例设置优先级可以生效
        FilterRegistrationBean<PassiveXssFilter> bean = new FilterRegistrationBean<>();
        //注册自定义过滤器
        bean.setFilter(new PassiveXssFilter());
        //过滤器名称
        bean.setName("xssDefense");
        //过滤所有路径
        bean.addUrlPatterns("/*");
        log.info("olympus-ares: initialize PassiveXssFilter success");
        return bean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "olympus.ares.passive-defense-config", name = "over-times-fusing", havingValue = "true")
    public AresDefenseWebConfigure registrationAresDefenseWebConfigure(){
        log.info("olympus-ares: initialize PassiveOverTimesFusingInterceptor...");
        return new AresDefenseWebConfigure(aresDefenseSpringConfiguration, passiveOverTimesFusingRole);
    }
}
