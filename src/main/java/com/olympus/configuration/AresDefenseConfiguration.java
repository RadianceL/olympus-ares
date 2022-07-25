package com.olympus.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * spring配置参数
 * since 7/25/22
 *
 * @author eddie
 */
@Data
@ConfigurationProperties("olympus.ares")
public class AresDefenseConfiguration {
    /**
     * 主动防御是否开启
     */
    private boolean initiativeDefense;

    /**
     * 被动防御是否开启
     */
    private boolean passiveDefense;
    /**
     * 被动防御系统
     */
    @NestedConfigurationProperty
    private PassiveDefenseConfig passiveDefenseConfig;

    /**
     * 被动防御配置项
     */
    @Data
    static class PassiveDefenseConfig {
        /**
         * xss系统防护
         */
        private boolean xssDefense;
        /**
         * 是否开启超载断路器
         */
        private boolean overTimesFusing;
        /**
         * 多次请求服务熔断拦截
         */
        @NestedConfigurationProperty
        private OverTimesFusingInterceptorConfig overTimesFusingInterceptorConfig;
    }

    @Data
    static class OverTimesFusingInterceptorConfig {
        /**
         * 窗口数量 （require >= 5）
         */
        private int windowSize;
        /**
         * 每个时间片的时长，以毫秒为单位
         */
        private int timeMillisPerSlice;
        /**
         * 在一个完整窗口期内允许通过的最大阈值(模糊)
         */
        private int threshold;
    }
}
