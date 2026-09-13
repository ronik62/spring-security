package com.example.secure_hello.controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController 
public class HelloController {
    
    @GetMapping("/public")
    public String getPublic(){
        return "This is a public endpoint";
    }
    @GetMapping("/hello")
    public String getHello(){
        return "Hello, Ronik";
    }
    @GetMapping("/admin")
    public String getAdmin(){
        return "Hello, admin";
    }
}
