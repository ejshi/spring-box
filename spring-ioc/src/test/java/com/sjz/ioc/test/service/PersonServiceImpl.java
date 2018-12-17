package com.sjz.ioc.test.service;

import com.sjz.ioc.annotations.Service;

/**
 * @author shijun.
 * @date 2018/12/17 11:27
 * @description ${description}
 */
@Service(name = "personService")
public class PersonServiceImpl implements IPersonService{

    @Override
    public String getPerson(String personName) {
        if("zhangsan".equals(personName)){
            return "{zhangsan,20}";
        }else if("lisi".equals(personName)){
            return "{lisi,50}";
        }
        return "{not exist}";
    }
}
