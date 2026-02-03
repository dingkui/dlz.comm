package com.dlz.comm.util;

import com.dlz.comm.json.JSONList;
import com.dlz.comm.json.JSONMap;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JacksonUtil工具类单元测试
 * 
 * 测试JacksonUtil的各种JSON处理功能
 * 
 * @author dk
 */
@DisplayName("JacksonUtil工具类测试")
class JacksonUtilTest {

    @Nested
    @DisplayName("基础功能测试")
    class BasicFunctionalityTests {

        @Test
        @DisplayName("获取ObjectMapper实例测试")
        void testGetInstance() {
            ObjectMapper mapper = JacksonUtil.getInstance();
            assertNotNull(mapper);
            assertSame(mapper, JacksonUtil.getInstance());
        }

        @Test
        @DisplayName("对象转JSON字符串测试")
        void testGetJson() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "测试");
            map.put("age", 25);
            map.put("active", true);
            
            String json = JacksonUtil.getJson(map);
            assertNotNull(json);
            assertTrue(json.contains("\"name\":\"测试\""));
            assertTrue(json.contains("\"age\":25"));
            assertTrue(json.contains("\"active\":true"));
        }

        @Test
        @DisplayName("对象转JSON字节数组测试")
        void testToJsonAsBytes() {
            Map<String, Object> map = new HashMap<>();
            map.put("test", "测试");
            
            byte[] bytes = JacksonUtil.toJsonAsBytes(map);
            assertNotNull(bytes);
            assertTrue(bytes.length > 0);
        }
    }

    @Nested
    @DisplayName("反序列化测试")
    class DeserializationTests {

        @Test
        @DisplayName("字符串反序列化为JSONMap测试")
        void testReadValueStringToJSONMap() {
            String json = "{\"name\":\"测试用户\",\"age\":30,\"active\":true}";
            JSONMap map = JacksonUtil.readValue(json);
            
            assertNotNull(map);
            assertEquals("测试用户", map.getStr("name"));
            assertEquals(Integer.valueOf(30), map.getInt("age"));
            assertEquals(Boolean.TRUE, map.getBoolean("active"));
        }

        @Test
        @DisplayName("字符串反序列化为指定类型测试")
        void testReadValueStringToSpecificType() {
            String json = "{\"name\":\"测试\",\"age\":25}";
            Map<String, Object> map = JacksonUtil.readValue(json, Map.class);
            assertEquals("测试", map.get("name"));
            assertEquals(Integer.valueOf(25), map.get("age"));
        }

        @Test
        @DisplayName("字符串反序列化为列表测试")
        void testReadListValue() {
            String jsonArray = "[\"元素1\", \"元素2\", \"元素3\"]";
            List<String> list = JacksonUtil.readListValue(jsonArray, String.class);
            
            assertEquals(3, list.size());
            assertEquals("元素1", list.get(0));
            assertEquals("元素2", list.get(1));
            assertEquals("元素3", list.get(2));
        }

        @Test
        @DisplayName("字符串反序列化为JSONList测试")
        void testReadList() {
            String jsonArray = "[\"元素1\", 123, true]";
            JSONList list = JacksonUtil.readList(jsonArray);
            
            assertEquals(3, list.size());
            assertEquals("元素1", list.getStr(0));
            assertEquals(Integer.valueOf(123), list.getInt(1));
            assertEquals(Boolean.TRUE, list.getBoolean(2));
        }

        @Test
        @DisplayName("无效JSON处理测试")
        void testInvalidJsonHandling() {
            JSONMap result = JacksonUtil.readValue("{invalid json}", JSONMap.class);
            assertNull(result);
            
            List<String> listResult = JacksonUtil.readListValue("{invalid json}", String.class);
            assertNull(listResult);
        }
    }

    @Nested
    @DisplayName("JsonNode操作测试")
    class JsonNodeTests {

        @Test
        @DisplayName("对象转JsonNode测试")
        void testValueToTree() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "测试");
            map.put("age", 25);
            
            JsonNode node = JacksonUtil.valueToTree(map);
            assertNotNull(node);
            assertTrue(node.has("name"));
            assertTrue(node.has("age"));
            assertEquals("测试", node.get("name").asText());
            assertEquals(25, node.get("age").asInt());
        }

        @Test
        @DisplayName("内容转JsonNode测试")
        void testReadTree() {
            String json = "{\"name\":\"测试\",\"nested\":{\"value\":123}}";
            JsonNode node = JacksonUtil.readTree(json);
            
            assertNotNull(node);
            assertEquals("测试", node.get("name").asText());
            assertEquals(123, node.get("nested").get("value").asInt());
        }
    }

    @Nested
    @DisplayName("类型转换测试")
    class TypeConversionTests {

        @Test
        @DisplayName("coverObj方法测试")
        void testCoverObj() {
            String jsonString = "{\"name\":\"测试\",\"age\":25}";
            JSONMap map = JacksonUtil.coverObj(jsonString, JSONMap.class);
            assertEquals("测试", map.getStr("name"));
            assertEquals(Integer.valueOf(25), map.getInt("age"));
            
            JSONMap sameMap = new JSONMap();
            sameMap.put("test", "值");
            JSONMap result = JacksonUtil.coverObj(sameMap, JSONMap.class);
            assertSame(sameMap, result);
            
            assertNull(JacksonUtil.coverObj(null, String.class));
        }

        @Test
        @DisplayName("convertValue方法测试")
        void testConvertValue() {
            Map<String, Object> sourceMap = new HashMap<>();
            sourceMap.put("name", "测试");
            sourceMap.put("age", 25);
            
            LinkedHashMap<String, Object> linkedMap = JacksonUtil.convertValue(sourceMap, LinkedHashMap.class);
            assertEquals("测试", linkedMap.get("name"));
            assertEquals(Integer.valueOf(25), linkedMap.get("age"));
        }

        @Test
        @DisplayName("canSerialize方法测试")
        void testCanSerialize() {
            assertTrue(JacksonUtil.canSerialize(null));
            assertTrue(JacksonUtil.canSerialize(new HashMap<>()));
            assertTrue(JacksonUtil.canSerialize("测试"));
            assertTrue(JacksonUtil.canSerialize(123));
        }
    }

    @Nested
    @DisplayName("路径取值测试")
    class PathExtractionTests {

        @Test
        @DisplayName("at方法基础测试")
        void testAtBasic() {
            String json = "{\"name\":\"测试\",\"age\":25,\"active\":true}";
            
            String name = (String) JacksonUtil.at(json, "name");
            assertEquals("测试", name);
            
            Integer age = (Integer) JacksonUtil.at(json, "age");
            assertEquals(Integer.valueOf(25), age);
            
            Boolean active = (Boolean) JacksonUtil.at(json, "active");
            assertEquals(Boolean.TRUE, active);
        }

        @Test
        @DisplayName("at方法嵌套对象测试")
        void testAtNestedObject() {
            String json = "{\"user\":{\"name\":\"测试用户\",\"profile\":{\"email\":\"test@example.com\"}}}";
            
            String email = (String) JacksonUtil.at(json, "user.profile.email");
            assertEquals("test@example.com", email);
            
            JSONMap userProfile = (JSONMap) JacksonUtil.at(json, "user.profile");
            assertEquals("test@example.com", userProfile.getStr("email"));
        }

        @Test
        @DisplayName("at方法数组索引测试")
        void testAtArrayIndex() {
            String json = "{\"items\":[{\"name\":\"项目1\"},{\"name\":\"项目2\"},{\"name\":\"项目3\"}]}";
            
            String itemName = (String) JacksonUtil.at(json, "items[1].name");
            assertEquals("项目2", itemName);
            
            String lastItemName = (String) JacksonUtil.at(json, "items[-1].name");
            assertEquals("项目3", lastItemName);
        }

        @Test
        @DisplayName("at方法指定类型测试")
        void testAtWithType() {
            String json = "{\"name\":\"测试用户\",\"age\":\"25\",\"score\":\"95.5\"}";
            
            String name = JacksonUtil.at(json, "name", String.class);
            assertEquals("测试用户", name);
            
            Integer age = JacksonUtil.at(json, "age", Integer.class);
            assertEquals(Integer.valueOf(25), age);
            
            Double score = JacksonUtil.at(json, "score", Double.class);
            assertEquals(Double.valueOf(95.5), score);
        }

        @Test
        @DisplayName("at方法边界情况测试")
        void testAtEdgeCases() {
            String json = "{\"data\":{\"items\":[1,2,3]}}";
            
            Object result1 = JacksonUtil.at(json, "");
            assertEquals(json, result1);
            
            Object result2 = JacksonUtil.at(null, "any.path");
            assertNull(result2);
            
            Object result3 = JacksonUtil.at(json, "nonexistent.path");
            assertNull(result3);

            String data="{" +
                    "\"info\":{\"a\":[" +
                    "                   [{\"b\":1},{\"c\":2}]," +
                    "                   [{\"d\":3},{\"e\":4},{\"f\":5}]" +
                    "                 ]" +
                    "          }" +
                    "}";
            assertEquals(2, JacksonUtil.at(data,"info.a[0][1].c"));
            assertEquals(5, JacksonUtil.at(data,"info.a[1][2].f"));
            assertEquals("{\"f\":5}", JacksonUtil.at(data,"info.a[1][2]").toString());
            assertEquals("{\"f\":5}", JacksonUtil.at(data,"info.a[1][-1]").toString());
        }
    }

    @Nested
    @DisplayName("JavaType构建测试")
    class JavaTypeConstructionTests {

        @Test
        @DisplayName("mkJavaType基础测试")
        void testMkJavaTypeBasic(){
            JavaType simpleType = JacksonUtil.mkJavaType(String.class);
            assertEquals(String.class, simpleType.getRawClass());
            assertEquals(0, simpleType.getBindings().size());
            
            JavaType listType = JacksonUtil.mkJavaType(List.class, String.class);
            assertEquals(List.class, listType.getRawClass());
            assertEquals(1, listType.getBindings().size());

            JavaType javaType = JacksonUtil.mkJavaType(Map.class,String.class,Float.class);
            Map<String,Float> b = JacksonUtil.readValue("{\"a\":1}",javaType);
            assertEquals(1.0f, b.get("a"));
        }
    }

    @Nested
    @DisplayName("JSON格式判断测试")
    class JsonFormatDetectionTests {

        @Test
        @DisplayName("isJsonObj方法测试")
        void testIsJsonObj() {
            assertTrue(JacksonUtil.isJsonObj("{}"));
            assertTrue(JacksonUtil.isJsonObj(" { } "));
            assertTrue(JacksonUtil.isJsonObj("{\"key\":\"value\"}"));
            
            assertFalse(JacksonUtil.isJsonObj("[]"));
            assertFalse(JacksonUtil.isJsonObj("[1,2,3]"));
            assertFalse(JacksonUtil.isJsonObj("plain text"));
        }

        @Test
        @DisplayName("isJsonArray方法测试")
        void testIsJsonArray() {
            assertTrue(JacksonUtil.isJsonArray("[]"));
            assertTrue(JacksonUtil.isJsonArray(" [ ] "));
            assertTrue(JacksonUtil.isJsonArray("[1,2,3]"));
            
            assertFalse(JacksonUtil.isJsonArray("{}"));
            assertFalse(JacksonUtil.isJsonArray("{\"key\":\"value\"}"));
            assertFalse(JacksonUtil.isJsonArray("plain text"));
        }
    }
}