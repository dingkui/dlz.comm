package com.dlz.test;

public class SimpleTest {
    public static void main(String[] args) {
        String a="select * from asdasd where aasd select xx from cc";
        int from = a.toLowerCase().indexOf(" from ");
        String countsql="select count(1) from"+a.substring(from+5);
        System.out.println(countsql);
        System.out.println(a.replaceAll("^(select )[\\*\\w,\\s]+( from.*)","$1count(1)$2"));
    }
}
