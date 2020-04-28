package com.gembaadvantage.apollo.gatewayservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class GatewayFallback {

    @GetMapping("/default")
    public FallbackResponse getDefault() {
        FallbackResponse a = new FallbackResponse();
        a.setMsgCode(500);
        a.setMsg("fallBackMsg");
        return a;
    }

}
