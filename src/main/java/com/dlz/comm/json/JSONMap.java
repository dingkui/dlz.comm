package com.dlz.comm.json;

import com.dlz.comm.exception.SystemException;
import com.dlz.comm.util.JacksonUtil;
import com.dlz.comm.util.StringUtils;
import com.dlz.comm.util.VAL;
import com.dlz.comm.util.ValUtil;

import java.util.*;

/**
 * JSON映射类
 *
 * 继承HashMap<String, Object>，实现了IUniversalVals接口，提供便捷的JSON数据操作功能
 *
 * @author dk 2017-06-15
 */
public class JSONMap extends HashMap<String, Object> implements IUniversalVals {
    /**
     * 序列化版本UID
     */
    private static final long serialVersionUID = 7554800764909179290L;

    /**
     * 无参构造函数
     */
    public JSONMap() {
        super();
    }

    /**
     * 使用对象构造JSONMap
     *
     * @param obj 源对象，可以是Map或其他对象
     */
    public JSONMap(Object obj) {
        super();
        if(obj == null) {
            return;
        }
        if(obj instanceof Map) {
            putAll((Map) obj);
        } else {
            String string = JacksonUtil.getJson(obj);
            if(string == null) {
                return;
            }
            putAll(JacksonUtil.readValue(string));
        }
    }

    /**
     * 使用字符序列构造JSONMap
     *
     * @param obj 字符序列，必须是有效的JSON字符串
     */
    public JSONMap(CharSequence obj) {
        super();
        if(obj == null) {
            return;
        }
        String str = obj.toString().trim().replaceAll("//.*", "");
        if(JacksonUtil.isJsonObj(str)) {
            putAll(JacksonUtil.readValue(str));
        } else {
            throw new SystemException("参数不能转换成JSONMap:" + str);
        }
    }

    /**
     * 使用键值对构造JSONMap
     *
     * @param key 第一个键
     * @param val 第一个值
     * @param value 键值对数组，必须是偶数个元素
     */
    public JSONMap(String key, Object val, Object... value) {
        super();
        int length = value.length;
        if(length % 2 == 1) {
            throw new SystemException("参数个数只能是偶数");
        }
        put(key, val);
        for(int index = 0; index < length - 1; index += 2) {
            if(!(value[index] instanceof String)) {
                throw new SystemException("键名必须为String");
            }
            put((String) value[index], value[index + 1]);
        }
    }

    /**
     * 创建JSONMap实例
     *
     * @param json 源JSON数据
     * @return JSONMap实例
     */
    public static JSONMap createJsonMap(Object json) {
        return new JSONMap(json);
    }

    /**
     * 将JSONMap转换为指定类型的Map
     *
     * @param objectClass 目标对象类型
     * @param <T> 目标类型泛型
     * @return 指定类型的Map
     */
    public <T> Map<String, T> asMap(Class<T> objectClass) {
        this.forEach((key, value) -> this.put(key, ValUtil.toObj(value, objectClass)));
        return (Map<String, T>) this;
    }

    /**
     * 将JSONMap转换为JSONMap类型的Map
     *
     * @return JSONMap类型的Map
     */
    public Map<String, JSONMap> asMap() {
        return asMap(JSONMap.class);
    }

    /**
     * 将JSONMap转换为JSONList类型的Map
     *
     * @return JSONList类型的Map
     */
    public Map<String, JSONList> asMapList() {
        return asMap(JSONList.class);
    }

    /**
     * 清除空属性 null，""
     *
     * @return 当前实例
     */
    public JSONMap clearEmptyProp() {
        List<String> emptyKeys = new ArrayList<>();
        for(Entry<String, Object> entry : this.entrySet()) {
            if(StringUtils.isEmpty(entry.getValue())) {
                emptyKeys.add(entry.getKey());
            }
        }
        for(String key : emptyKeys) {
            this.remove(key);
        }
        return this;
    }

    /**
     * 按层次设定值，设定的对象需要是JSONMap对象,如果对应的父值不存在则自动创建
     *
     * @param key 多级键，
     *   如：a.b.c.d
     *       a[0][1].c.d
     *       a[0].b[1].d
     *       a.b[1].c  
     * @param value 要设定的值
     * @return 当前实例
     */
    public JSONMap set(String key, Object value) {
        if(StringUtils.isEmpty(key)) {
            throw new SystemException("key不能为空");
        }
        
        // 使用 splitKey1 拆分键
        VAL<String, String> keys = JacksonUtil.splitKey(key);
        String currentKey = keys.v1;
        String remainingKey = keys.v2;
        
        // 判断当前键是否包含数组索引
        int bracketIndex = currentKey.indexOf("[");
        
        if(bracketIndex == -1) {
            // 情况1: 普通键，如 a 或 user
            if(remainingKey == null) {
                // 已经是最后一级，直接设置值
                put(currentKey, value);
            } else {
                // 还有下一级，需要获取或创建 JSONMap
                Object existing = get(currentKey);
                JSONMap childMap;
                
                if(existing == null) {
                    childMap = new JSONMap();
                    put(currentKey, childMap);
                } else if(existing instanceof JSONMap) {
                    childMap = (JSONMap) existing;
                } else {
                    throw new SystemException("键 '" + currentKey + "' 的值类型不匹配，期望 JSONMap，实际为 " + existing.getClass().getSimpleName());
                }
                
                childMap.set(remainingKey, value);
            }
        } else {
            // 情况2: 包含数组索引，如 a[0] 或 [0]
            handleArrayKey(currentKey, remainingKey, value);
        }
        
        return this;
    }
    
    /**
     * 处理包含数组索引的键
     * 
     * @param currentKey 当前键，如 a[0][1] 或 [0]
     * @param remainingKey 剩余键
     * @param value 要设置的值
     */
    private void handleArrayKey(String currentKey, String remainingKey, Object value) {
        // 解析数组键，如 a[0][1] -> arrayName=a, indices=[0,1]
        String arrayName;
        List<Integer> indices = new ArrayList<>();
        
        if(currentKey.startsWith("[")) {
            // 以 [ 开头，如 [0] 或 [0][1]
            arrayName = "";
            parseArrayIndices(currentKey, 0, indices);
        } else {
            // 普通形式，如 a[0] 或 a[0][1]
            int firstBracket = currentKey.indexOf("[");
            arrayName = currentKey.substring(0, firstBracket);
            parseArrayIndices(currentKey, firstBracket, indices);
        }
        
        // 获取或创建数组
        Object existing = arrayName.isEmpty() ? null : get(arrayName);
        JSONList list;
        
        if(existing == null) {
            list = new JSONList();
            if(!arrayName.isEmpty()) {
                put(arrayName, list);
            }
        } else if(existing instanceof JSONList) {
            list = (JSONList) existing;
        } else if(existing instanceof List) {
            list = new JSONList((List<?>) existing);
            put(arrayName, list);
        } else {
            throw new SystemException("键 '" + arrayName + "' 的值类型不匹配，期望 JSONList，实际为 " + existing.getClass().getSimpleName());
        }
        
        // 递归处理多维数组索引
        setArrayValue(list, indices, 0, remainingKey, value);
    }
    
    /**
     * 解析数组索引，如 [0][1][2] -> [0, 1, 2]
     * 
     * @param key 包含数组索引的键
     * @param startPos 开始位置
     * @param indices 输出的索引列表
     */
    private void parseArrayIndices(String key, int startPos, List<Integer> indices) {
        int pos = startPos;
        while(pos < key.length() && key.charAt(pos) == '[') {
            int rightBracket = key.indexOf(']', pos);
            if(rightBracket == -1) {
                throw new SystemException("数组下标格式错误，缺少右括号: " + key);
            }
            
            String indexStr = key.substring(pos + 1, rightBracket);
            if(indexStr.isEmpty()) {
                throw new SystemException("数组下标不能为空: " + key);
            }
            
            try {
                int index = Integer.parseInt(indexStr);
                indices.add(index);
            } catch(NumberFormatException e) {
                throw new SystemException("数组下标必须是整数: " + indexStr);
            }
            
            pos = rightBracket + 1;
        }
    }
    
    /**
     * 递归设置数组值
     * 
     * @param list 当前数组
     * @param indices 索引列表
     * @param currentLevel 当前处理的索引层级
     * @param remainingKey 剩余键
     * @param value 要设置的值
     */
    private void setArrayValue(JSONList list, List<Integer> indices, int currentLevel, String remainingKey, Object value) {
        int index = indices.get(currentLevel);
        
        // 处理负数索引
        if(index < 0) {
            index = list.size() + index;
            if(index < 0) {
                index = 0;
            }
        }
        
        // 扩展数组大小
        while(list.size() <= index) {
            list.add(null);
        }
        
        if(currentLevel == indices.size() - 1) {
            // 最后一个索引
            if(remainingKey == null) {
                // 没有剩余键，直接设置值
                list.set(index, value);
            } else {
                // 还有剩余键，需要获取或创建 JSONMap
                Object existing = list.get(index);
                JSONMap childMap;
                
                if(existing == null) {
                    childMap = new JSONMap();
                    list.set(index, childMap);
                } else if(existing instanceof JSONMap) {
                    childMap = (JSONMap) existing;
                } else {
                    throw new SystemException("数组索引 [" + index + "] 的值类型不匹配，期望 JSONMap，实际为 " + existing.getClass().getSimpleName());
                }
                
                childMap.set(remainingKey, value);
            }
        } else {
            // 还有更多索引，需要获取或创建嵌套数组
            Object existing = list.get(index);
            JSONList childList;
            
            if(existing == null) {
                childList = new JSONList();
                list.set(index, childList);
            } else if(existing instanceof JSONList) {
                childList = (JSONList) existing;
            } else if(existing instanceof List) {
                childList = new JSONList((List<?>) existing);
                list.set(index, childList);
            } else {
                throw new SystemException("数组索引 [" + index + "] 的值类型不匹配，期望 JSONList，实际为 " + existing.getClass().getSimpleName());
            }
            
            setArrayValue(childList, indices, currentLevel + 1, remainingKey, value);
        }
    }

    /**
     * 将值添加到JSONMap的列表中
     *
     * @param key 设定的key 支持多级穿透，如：a[0][1].c.d
     * @param obj 设定对象
     * @return 当前实例
     */
    public JSONMap add(String key, Object obj) {
        List<Object> list = this.getList(key,new JSONList());
        list.addAll(ValUtil.toList(obj));
        set(key, list);
        return this;
    }

    /**
     * 设置键值对
     *
     * @param key 键
     * @param value 值
     * @return 当前实例
     */
    @Override
    public JSONMap put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 返回JSON字符串表示
     *
     * @return JSON字符串
     */
    @Override
    public String toString() {
        return JacksonUtil.getJson(this);
    }

    /**
     * 获取信息对象
     *
     * @return 信息对象（即自身）
     */
    @Override
    public Object getInfoObject() {
        return this;
    }
}