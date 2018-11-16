package com.sjz.ioc.test.model;

public class Mouth {

    private String name;

    public Mouth(String name) {
        this.name = name;
    }

    public void speak() {

        System.out.println(name + ",say hello world");

    }

}
