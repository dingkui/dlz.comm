package com.dlz.test.tools;
/*
 * Created with Intellij IDEA
 * USER: 焦一平
 * Mail: jiaoyiping@gmail.com
 * Date: 2016/12/4
 * Time: 9:28
 * To change this template use File | Settings | Editor | File and Code Templates
 */

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericTypeTest {
    static class Test1 extends TE<String> {
    }

    static class Test2 implements IE<String> {
    }
    interface Test3 extends IE<String> {
    }

    public static void main(String[] args) {
        //获取类定义上的泛型类型
        Type types = Test1.class.getGenericSuperclass();

        Type[] genericType = ((ParameterizedType) types).getActualTypeArguments();
        for (Type t : genericType) {
            System.out.println(t.getTypeName());
        }
        System.out.println("===============================================");

        //获取接口定义上的泛型类型
        //一个类可能实现多个接口,每个接口上定义的泛型类型都可取到
        Type[] interfacesTypes = Test2.class.getGenericInterfaces();
        for (Type t : interfacesTypes) {
            Type[] genericType2 = ((ParameterizedType) t).getActualTypeArguments();
            for (Type t2 : genericType2) {
                System.out.println(t2.getTypeName());
            }
        }

        System.out.println("===============================================");

        //获取接口定义上的泛型类型
        //一个类可能实现多个接口,每个接口上定义的泛型类型都可取到
        Type[] interfacesTypes3 = Test3.class.getGenericInterfaces();
        for (Type t : interfacesTypes) {
            Type[] genericType2 = ((ParameterizedType) t).getActualTypeArguments();
            for (Type t2 : genericType2) {
                System.out.println(t2.getTypeName());
            }
        }
        System.out.println("===============================================");
        System.out.println(Test3.class.getSuperclass());
        System.out.println(IE.class.isAssignableFrom(Test3.class));
        System.out.println(Test3.class);


    }
}

class TE<T1> {
}

interface IE<T1> {
}