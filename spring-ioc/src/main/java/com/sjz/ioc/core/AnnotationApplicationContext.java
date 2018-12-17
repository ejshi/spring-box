package com.sjz.ioc.core;

import com.sjz.ioc.annotations.Service;
import com.sjz.ioc.bean.BeanDefinition;
import com.sjz.ioc.utils.AnnotationUtil;
import com.sjz.ioc.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * 注解实现Bean注入
 */
public class AnnotationApplicationContext extends BeanFactoryImpl {

    /**
     * 全限定包名
     */
    private String packageName;

    public AnnotationApplicationContext(String packageName) {
        this.packageName = packageName;
    }

    public void loadBean(){
        AnnotationUtil instance = AnnotationUtil.getInstance(this.packageName);

        Set<Class<?>> classSet = instance.getAnnotation(Service.class);

        BeanFactoryImpl beanFactory = new BeanFactoryImpl();

        classSet.stream().forEach(aClass -> {
            Service serviceAnnotation = aClass.getAnnotation(Service.class);
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setAClass(aClass);

            //优先使用注解定义的beanName
            String serviceName = StringUtil.isBlank(serviceAnnotation.name()) ? aClass.getSimpleName() : serviceAnnotation.name();

            beanDefinition.setName(serviceName);
            beanDefinition.setClassName(aClass.getName());
            beanFactory.registerBean(StringUtils.uncapitalize(serviceName), beanDefinition);
        });
    }
}
