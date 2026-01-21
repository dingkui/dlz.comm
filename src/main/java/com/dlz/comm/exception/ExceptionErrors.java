package com.dlz.comm.exception;


import java.util.HashMap;
import java.util.Map;

public class ExceptionErrors {
    public static Map<Integer, String> errors = new HashMap<>();

    static {
        addErrors(6001, "系统异常");
    }

    public static void addErrors(int code, String info) {
        if (errors.containsKey(code)) {
            throw new SystemException("code is exsits:" + code);
        }
        errors.put(code, info);
    }

    public static String getInfo(int code) {
        String info = errors.get(code);
        if (info == null) {
            throw new SystemException("code is no exsits:" + code);
        }
        return info;
    }
}