package com.dlz.test.beans;

/**
 * 测试实体类
 */
public class TestEntity {
    private String name;
    private int age;
    private boolean active;
    public static String staticField = "staticValue";
    
    // 构造函数
    public TestEntity() {}
    
    public TestEntity(String name, int age, boolean active) {
        this.name = name;
        this.age = age;
        this.active = active;
    }
    
    // Getter和Setter方法
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "TestEntity{name='" + name + "', age=" + age + ", active=" + active + "}";
    }
}