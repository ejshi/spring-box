package com.sjz.ioc.test.service;

import com.sjz.ioc.annotations.Service;

/**
 * @author shijun.
 * @date 2018/12/13 17:55
 * @description 测试数据
 */
@Service
public class HelloService {

    public void hello(){
        System.out.println("============= hello world ============");
    }
}
