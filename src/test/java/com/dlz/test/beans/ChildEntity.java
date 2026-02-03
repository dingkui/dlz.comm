package com.dlz.test.beans;

/**
 * 继承测试实体类
 */
public class ChildEntity extends TestEntity {
    private String childField;
    private double score;
    
    public ChildEntity() {
        super();
    }
    
    public ChildEntity(String name, int age, boolean active, String childField, double score) {
        super(name, age, active);
        this.childField = childField;
        this.score = score;
    }
    
    public String getChildField() {
        return childField;
    }
    
    public void setChildField(String childField) {
        this.childField = childField;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    @Override
    public String toString() {
        return "ChildEntity{" +
                "childField='" + childField + "', score=" + score +
                ", parent=" + super.toString() + "}";
    }
}