package com.dlz.comm.util;

import com.dlz.comm.json.JSONMap;
import com.dlz.test.beans.SourceBean;
import com.dlz.test.beans.TargetBean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ValUtil工具类单元测试
 * 
 * 测试ValUtil的各种类型转换和对象操作功能
 * 
 * @author dk
 */
@DisplayName("BeanUtil工具类测试")
class BeanUtilTest {
    @Nested
    @DisplayName("属性访问测试")
    class PropertyAccessTests {

        class TestBean {
            private String name = "测试名称";
            private int age = 25;
            private List<String> tags = Arrays.asList("标签1", "标签2");
            
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public int getAge() { return age; }
            public void setAge(int age) { this.age = age; }
            public List<String> getTags() { return tags; }
            public void setTags(List<String> tags) { this.tags = tags; }
        }

        @Test
        @DisplayName("getValue方法测试")
        void testGetValue() {
            TestBean bean = new TestBean();
            
            // 直接属性访问
            String name = BeanUtil.getValue(bean, "name", false);
            assertEquals("测试名称", name);
            
            Integer age = BeanUtil.getValue(bean, "age", false);
            assertEquals(Integer.valueOf(25), age);
            
            // Map访问
            Map<String, Object> map = new HashMap<>();
            map.put("key1", "值1");
            map.put("key2", 123);
            assertEquals("值1", BeanUtil.getValue(map, "key1", false));
            assertEquals(Integer.valueOf(123), BeanUtil.getValue(map, "key2", false));
            
            // List访问
            List<String> list = Arrays.asList("元素1", "元素2", "元素3");
            assertEquals("元素2", BeanUtil.getValue(list, "1", false));
            
            // JSON字符串访问
            String json = "{\"name\":\"JSON测试\",\"nested\":{\"value\":999}}";
            assertEquals("JSON测试", BeanUtil.getValue(json, "name", false));
            assertEquals(Integer.valueOf(999), BeanUtil.getValue(json, "nested.value", false));
            
            // 数组访问
            String[] array = {"数组元素1", "数组元素2"};
            assertEquals("数组元素1", BeanUtil.getValue(array, "0", false));
        }

        @Test
        @DisplayName("setValue方法测试")
        void testSetValue() {
            TestBean bean = new TestBean();
            
            // 设置简单属性
            BeanUtil.setValue(bean, "name", "新名称", false);
            assertEquals("新名称", bean.getName());
            
            BeanUtil.setValue(bean, "age", 30, false);
            assertEquals(30, bean.getAge());
            
            // Map设置
            Map<String, Object> map = new HashMap<>();
            BeanUtil.setValue(map, "newKey", "新值", false);
            assertEquals("新值", map.get("newKey"));
            
            // List设置
            List<String> list = new ArrayList<>(Arrays.asList("元素1", "元素2"));
            BeanUtil.setValue(list, "1", "修改的元素", false);
            assertEquals("修改的元素", list.get(1));
            
            // JSON字符串设置
            String json = "{\"name\":\"原始名称\"}";
            String newJson = (String) BeanUtil.setValue(json, "name", "修改名称", false);
            JSONMap resultMap = new JSONMap(newJson);
            assertEquals("修改名称", resultMap.getStr("name"));
        }
    }

    @Nested
    @DisplayName("集合操作测试")
    class CollectionOperationTests {

        @Test
        @DisplayName("join方法测试")
        void testJoin() {
            // Map join
            Map<String, Object> targetMap = new HashMap<>();
            targetMap.put("existing", "已存在");
            
            Map<String, Object> sourceMap = new HashMap<>();
            sourceMap.put("newKey", "新值");
            sourceMap.put("another", "另一个值");

            BeanUtil.join(sourceMap, targetMap);
            assertEquals(3, targetMap.size());
            assertEquals("已存在", targetMap.get("existing"));
            assertEquals("新值", targetMap.get("newKey"));
            assertEquals("另一个值", targetMap.get("another"));
            
            // List join
            List<String> targetList = new ArrayList<>();
            targetList.add("已存在元素");
            
            List<String> sourceList = Arrays.asList("新元素1", "新元素2");
            BeanUtil.join(sourceList, targetList);
            assertEquals(3, targetList.size());
            assertEquals("已存在元素", targetList.get(0));
            assertEquals("新元素1", targetList.get(1));
            assertEquals("新元素2", targetList.get(2));
        }
    }

    @Nested
    @DisplayName("对象拷贝测试")
    class ObjectCopyTests {
        @Test
        @DisplayName("普通对象拷贝测试")
        void testCopy() {
            SourceBean source = new SourceBean();
            TargetBean target = new TargetBean();
            
            TargetBean result = BeanUtil.copyAsTarget(source, target, false);
            
            assertEquals("源名称", result.getName());
            assertEquals(30, result.getAge());
            assertSame(target, result);
        }

        @Test
        @DisplayName("JSONMap对象拷贝测试")
        void testCopyToJSONMap() {
            SourceBean source = new SourceBean();
            JSONMap target = new JSONMap();

            BeanUtil.copyAsSource(source, target, false);
            
            assertEquals("源名称", target.getStr("name"));
            assertEquals(Integer.valueOf(30), target.getInt("age"));
            assertEquals("test@example.com", target.getStr("email"));
        }
    }
}