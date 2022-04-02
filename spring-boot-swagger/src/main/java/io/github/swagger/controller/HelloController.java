package io.github.swagger.controller;

import io.github.swagger.domain.User;
import io.github.swagger.service.CacheService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hello")
@Api(tags = "hello")
@AllArgsConstructor
public class HelloController {

    CacheService cacheService;

    @GetMapping
    public String hello() {
        return "hello";
    }

    @GetMapping("/user")
    public List<User> userAll() {
        return cacheService.users();
    }
}