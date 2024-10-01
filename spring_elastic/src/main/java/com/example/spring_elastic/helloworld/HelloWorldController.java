package com.example.spring_elastic.helloworld;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello, World Great jai parvathi amm oam namo venkatesaya namaha oam!";
    }
}
