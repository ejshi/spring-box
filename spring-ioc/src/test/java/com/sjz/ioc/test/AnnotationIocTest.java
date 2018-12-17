package com.sjz.ioc.test;

import com.sjz.ioc.core.AnnotationApplicationContext;
import com.sjz.ioc.test.service.HelloService;
import com.sjz.ioc.test.service.IPersonService;

/**
 * @author shijun.
 * @date 2018/12/17 11:09
 * @description ${description}
 */
public class AnnotationIocTest {
    public static void main(String[] args) {

        AnnotationApplicationContext applicationContext = new AnnotationApplicationContext("com.sjz.ioc");
        applicationContext.loadBean();

        HelloService helloService = (HelloService)applicationContext.getBean("helloService");
        helloService.hello();

        IPersonService iPersonService = (IPersonService)applicationContext.getBean("personService");
        String person = iPersonService.getPerson("zhangsan");
        System.out.println(person);
    }
}
