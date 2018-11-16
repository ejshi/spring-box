package com.sjz.ioc.core;

import com.google.common.base.Preconditions;
import com.sjz.ioc.bean.BeanDefinition;
import com.sjz.ioc.bean.ConstructorArg;
import com.sjz.ioc.utils.BeanUtils;
import com.sjz.ioc.utils.ClassUtils;
import com.sjz.ioc.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BeanFactoryImpl implements  BeanFactory {

    private static final ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private static final Set<String> beanNameSet = Collections.synchronizedSet(new HashSet<>());


    @Override
    public Object getBean(String beanName) {
        Preconditions.checkNotNull(beanName, "beanName不能为空");
        Object bean = beanMap.get(beanName);
        if(bean != null){
            return bean;
        }

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Preconditions.checkNotNull(beanDefinition, "beanName没有被定义");

        //create bean
        try {
            bean = createBean(beanDefinition);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("bean("+beanName+")创建异常");
        }

        //bean属性注入
        if(bean != null){
            try {
                populatebean(bean);
                beanMap.put(beanName, bean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException("bean("+beanName+")属性注入失败异常");
            }
        }
        return bean;
    }

    /**
     * 注入bean
     * @param beanName
     * @param beanDefinition
     */
    protected void registerBean(String beanName ,BeanDefinition beanDefinition){
        if(beanDefinitionMap.contains(beanName)){
            throw new IllegalArgumentException("bean（"+beanName+"）重复定义");
        }
        beanDefinitionMap.put(beanName, beanDefinition);
        beanNameSet.add(beanName);

    }

    /**
     * 创建bean
     * @param beanDefinition
     * @return
     * @throws Exception
     */
    private Object createBean(BeanDefinition beanDefinition) throws Exception {
        String className = beanDefinition.getClassName();
        Class cls = ClassUtils.loadClass(className);
        Preconditions.checkNotNull(cls, String.format("类（%s）找不到",className));

        List<ConstructorArg> constructorArgs = beanDefinition.getConstructorArgs();
        if(constructorArgs == null || constructorArgs.isEmpty()){
            return BeanUtils.instanceByCglib(cls, null, null);
        }

        List<Object> objects = new ArrayList<>();
        for (ConstructorArg constructorArg : constructorArgs) {
            if(null == constructorArg.getValue() && StringUtils.isBlank(constructorArg.getRef())){
                continue;
            }
            if(null != constructorArg.getValue()){
                objects.add(constructorArg.getValue());
            }else{
                objects.add(constructorArg.getRef());
            }
        }

        Class[] constructorArgClasses = objects.stream().map(obj -> obj.getClass())
                .collect(Collectors.toList()).toArray(new Class[0]);

        Constructor constructor = cls.getConstructor(constructorArgClasses);

        return BeanUtils.instanceByCglib(cls, constructor, objects.toArray(new Object[0]));
    }

    /**
     * 属性注入
     * @param bean
     * @throws IllegalAccessException
     */
    private void populatebean(Object bean) throws IllegalAccessException {

        Field[] fields = bean.getClass().getDeclaredFields();
        if(fields == null || fields.length == 0){
            return;
        }
        for (Field field : fields) {
            String beanName = field.getName();
            //beanName首字母小写
            beanName = StringUtils.uncapitalize(beanName);
            if(beanNameSet.contains(beanName)){
                //TODO 循环依赖判断
                Object fieldBean = getBean(beanName);
                ReflectionUtils.injectField(field, bean, fieldBean);
            }
        }

    }



}
