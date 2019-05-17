package com.sjz.ioc.test;


import com.sjz.ioc.core.AnnotationApplicationContext;
import com.sjz.ioc.core.JsonApplicationContext;
import com.sjz.ioc.test.model.Hand;
import com.sjz.ioc.test.model.Mouth;
import com.sjz.ioc.test.service.HelloService;
import org.apache.commons.lang3.StringUtils;

public class IocTest {

    public static void main(String[] args) throws Exception {

        JsonApplicationContext applicationContext = new JsonApplicationContext("application.json");
        applicationContext.init();

        System.out.println("=============================");
        
        Hand hand = (Hand) applicationContext.getBean("hand");
        hand.waveHand();

        Mouth mouth = (Mouth) applicationContext.getBean("mouth");
        mouth.speak();

    }
}
