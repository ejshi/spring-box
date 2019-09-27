package com.sjz.mvc.service;

import com.sjz.mvc.annotations.Service;

/**
 * @author shijun.
 * @date 2019/9/27 15:47
 * @description ${description}
 */
@Service
public class HelloService {

    public String hello(String name){

        return "hello service " + name;
    }
}
