package com.olympus.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * since 7/25/22
 *
 * @author eddie
 */
@Slf4j
@RestController
public class AresDefenseTestController {

    @GetMapping("/test")
    public String test(String test) {
        return test;
    }
}
