package com.sjz.mvc.controller;

import com.sjz.mvc.annotations.Controller;
import com.sjz.mvc.annotations.RequestMapping;
import com.sjz.mvc.annotations.Resource;
import com.sjz.mvc.service.HelloService;

/**
 * @author shijun.
 * @date 2019/9/27 11:17
 * @description spring-mvc测试类，和正常的springmvc写法一样
 */
@Controller
@RequestMapping(value = "/demo")
public class DemoController {

    @Resource
    private HelloService helloService;

    @RequestMapping(value = "/hello")
    public String hello(String name){
        System.out.println("==============开始访问===hello");
        return "hello " + name;
    }

    @RequestMapping(value = "/helloService")
    public String helloService(String name){
        System.out.println("==============开始访问===helloService");
        return helloService.hello(name);
    }
}
