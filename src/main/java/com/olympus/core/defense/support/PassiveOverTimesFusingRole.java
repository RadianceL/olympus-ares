package com.olympus.core.defense.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 多次访问熔断拦截自定义接口
 * since 7/27/22
 *
 * @author eddie
 */
public interface PassiveOverTimesFusingRole {

    /**
     * 通过请求返回本次访问的key
     * @param request           请求
     * @param response          返回
     * @return                  本次访问的key
     */
    String defineRequestRecordKey(HttpServletRequest request, HttpServletResponse response);

}
