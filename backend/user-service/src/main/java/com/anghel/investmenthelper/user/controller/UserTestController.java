package com.anghel.investmenthelper.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserTestController {

    @GetMapping("/api/users/hello")
    public String hello() {
        return "Hello from user-service";
    }
}
