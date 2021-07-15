package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lenovo
 * @version 1.0
 * @Date 2021/7/15 9:54
 * @Description
 */
@RestController
public class TestController {


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/set")
    public boolean setdata()
    {
        redisTemplate.opsForValue().set("key","11111");
        return true;
    }

    @GetMapping("/get")
    public String getdata()
    {
        Object object=redisTemplate.opsForValue().get("key");
        return object.toString();
    }


}
