package com.sjz.ioc.bean;

import lombok.Data;

import java.util.List;

@Data
public class BeanDefinition {

    private Class<?> aClass;

    private String name;

    private String className;

    private String interfaceName;

    /**
     * 构造函数的传入列表
     */
    private List<ConstructorArg> constructorArgs;

    /**
     * 注入的参数列表
     */
    private List<PropertyArg> propertyArgs;

}
