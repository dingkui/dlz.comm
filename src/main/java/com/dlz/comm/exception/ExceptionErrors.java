package com.dlz.comm.exception;


import java.util.HashMap;
import java.util.Map;

public class ExceptionErrors {
    public static Map<Integer, String> errors = new HashMap<>();

    static {
        addErrors(9997, "JSP内容异常");
        addErrors(9998, "参数提交异常");
        addErrors(9999, "JSP异常");
        addErrors(1000, "数据库连接异常");
        addErrors(1001, "数据库执行sql异常");
        addErrors(1002, "数据库参数校验异常");
        addErrors(1003, "数据库执行其他异常");
        addErrors(1004, "数据库结果异常");
        addErrors(1005, "数据转换异常");
        addErrors(2001, "非法访问异常");
        addErrors(3001, "业务异常");
        addErrors(3002, "代码异常");
        addErrors(3004, "代码异常");
        addErrors(3003, "校验错误");
        addErrors(4001, "批处理异常");
        addErrors(5002, "参数异常");
        addErrors(6001, "系统异常");
        addErrors(7000, "远程服务器连接失败");
        addErrors(7001, "远程调用异常");
        addErrors(7002, "远程调用数据读取异常");
        addErrors(7003, "Http状态非正常");
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