package com.olympus.configuration;

import com.olympus.core.defense.filter.InitiativeXssFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 阿瑞斯战神防御体系自动配置
 * since 7/25/22
 *
 * @author eddie
 */
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AresDefenseAutoConfiguration {
    /**
     * 滑动时间窗
     */
    private final AresDefenseSpringConfiguration aresDefenseSpringConfiguration;

    @Bean
    public FilterRegistrationBean<InitiativeXssFilter> registrationInitiativeXssFilter(){
        //通过FilterRegistrationBean实例设置优先级可以生效
        FilterRegistrationBean<InitiativeXssFilter> bean = new FilterRegistrationBean<>();
        //注册自定义过滤器
        bean.setFilter(new InitiativeXssFilter());
        //过滤器名称
        bean.setName("xssDefense");
        //过滤所有路径
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    public AresDefenseWebConfigure registrationAresDefenseWebConfigure(){
        return new AresDefenseWebConfigure(aresDefenseSpringConfiguration);
    }
}
