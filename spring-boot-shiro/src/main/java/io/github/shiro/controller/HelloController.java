package io.github.shiro.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@Api(tags = "hello")
public class HelloController {

    @GetMapping
    public String hello() {
        return "hello";
    }
}
