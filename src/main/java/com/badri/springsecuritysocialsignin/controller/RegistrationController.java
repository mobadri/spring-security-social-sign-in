package com.badri.springsecuritysocialsignin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {


    @GetMapping("/home")
    public String greet() {
        return "Welcome !!!";
    }

    @GetMapping("/user")
    public String user() {
        return "Welcome User!!!";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Welcome Admin!!!";
    }

    @GetMapping("/oidc")
    public String oidc() {
        return "Welcome OIDC User!!!";
    }

}
