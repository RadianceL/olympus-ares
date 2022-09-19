package com.olympus.demo.service;

import com.olympus.core.defense.support.PassiveOverTimesFusingRole;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * since 9/19/22
 *
 * @author eddie
 */
@Component
public class PassiveOverTimesFusingRoleImpl implements PassiveOverTimesFusingRole {

    @Override
    public String defineRequestRecordKey(HttpServletRequest request, HttpServletResponse response) {
        return request.getRequestURI();
    }

    @Override
    public void onHandlerRejectResponse(HttpServletRequest request, HttpServletResponse response) {

    }
}
