package com.dlz.comm.performance;

import com.dlz.comm.json.JSONMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * JSONMap 性能测试 - 优化版
 *
 * 输出格式：表格形式，一次测试一行
 */
public class JSONMapPerformanceTest {

    private static final String JSON_DATA = "        {\n" +
        "            \"code\": 0,\n" +
        "            \"message\": \"success\",\n" +
        "            \"data\": {\n" +
        "                \"order\": {\n" +
        "                    \"orderId\": \"WX20240205001\",\n" +
        "                    \"transactionId\": \"4200001234567890\",\n" +
        "                    \"status\": 1,\n" +
        "                    \"amount\": 9900\n" +
        "                },\n" +
        "                \"user\": {\n" +
        "                    \"openid\": \"oUpF8uMuAJO_M2pxb1Q9zNjWeS6o\"\n" +
        "                }\n" +
        "            }\n" +
        "        }";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @BeforeAll
    static void setup() {
        System.out.println("========================================");
        System.out.println("JSONMap 性能测试: 采用stream");
        System.out.println("========================================");
    }

    @Test
    void runAllPerformanceTests() {
        System.out.println("\n【场景 1】构造 1 次，读取多次");
        System.out.println("┌─────────────────┬──────────────┬────────────────────────┬────────────────────────┐");
        System.out.println("│ 读取次数         │ 传统方式       │ JSONMap 直接路径         │ JSONMap 子对象         │");
        System.out.println("├─────────────────┼──────────────┼────────────────────────┼────────────────────────┤");

        testReadPerformance(100, false);
        testReadPerformance(1000, false);
        testReadPerformance(10_000, false);
        testReadPerformance(50_000, false);
        testReadPerformance(100_000, false);
        testReadPerformance(1000_000, false);
        testReadPerformance(5000_000, false);

        System.out.println("└─────────────────┴──────────────┴────────────────────────┴────────────────────────┘");
        System.out.println("\n【场景 1】构造 1 次，读取多次。多线程执行");
        System.out.println("┌─────────────────┬──────────────┬────────────────────────┬────────────────────────┐");
        System.out.println("│ 读取次数         │ 传统方式       │ JSONMap 直接路径         │ JSONMap 子对象         │");
        System.out.println("├─────────────────┼──────────────┼────────────────────────┼────────────────────────┤");

        testReadPerformance(100, true);
        testReadPerformance(1000, true);
        testReadPerformance(10_000, true);
        testReadPerformance(50_000, true);
        testReadPerformance(100_000, true);
        testReadPerformance(1000_000, true);
        testReadPerformance(5000_000, true);

        System.out.println("└─────────────────┴──────────────┴────────────────────────┴────────────────────────┘");

        System.out.println("\n【场景 2】构造和读取多次");
        System.out.println("┌─────────────────┬──────────────┬────────────────────────┬────────────────────────┐");
        System.out.println("│ 次数             │ 传统方式      │ JSONMap 直接路径         │ JSONMap 子对象         │");
        System.out.println("├─────────────────┼──────────────┼────────────────────────┼────────────────────────┤");
        testConstructAndReadPerformance(1000,false);
        testConstructAndReadPerformance(10_000,false);
        testConstructAndReadPerformance(50_000,false);
        testConstructAndReadPerformance(100_000,false);
        testConstructAndReadPerformance(1000_000,false);
        testConstructAndReadPerformance(5000_000,false);
        testConstructAndReadPerformance(10_000_000,false);
        testConstructAndReadPerformance(100_000_000,false);
        System.out.println("└─────────────────┴──────────────┴────────────────────────┴────────────────────────┘");


        System.out.println("\n【场景 2】构造和读取多次。多线程执行");
        System.out.println("┌─────────────────┬──────────────┬────────────────────────┬────────────────────────┐");
        System.out.println("│ 次数             │ 传统方式      │ JSONMap 直接路径         │ JSONMap 子对象         │");
        System.out.println("├─────────────────┼──────────────┼────────────────────────┼────────────────────────┤");
        testConstructAndReadPerformance(1000,true);
        testConstructAndReadPerformance(10_000,true);
        testConstructAndReadPerformance(50_000,true);
        testConstructAndReadPerformance(100_000,true);
        testConstructAndReadPerformance(1000_000,true);
        testConstructAndReadPerformance(5000_000,true);
        testConstructAndReadPerformance(10_000_000,true);
        testConstructAndReadPerformance(100_000_000,true);
        System.out.println("└─────────────────┴──────────────┴────────────────────────┴────────────────────────┘");

        System.out.println("\n========================================");
        System.out.println("测试完成！");
        System.out.println("========================================");
    }

    // ==================== 场景 1：构造 1 次，读取多次 ====================
    private void testReadPerformance(int iterations,boolean multiThread) {
        long time1 = test_Read(iterations,multiThread,()-> {
            try {
                return OBJECT_MAPPER.readValue(JSON_DATA, Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        },JSONMapPerformanceTest::readTraditionalWay);
        long time2 = test_Read(iterations,multiThread,()-> JSONMap.createWithJson(JSON_DATA),JSONMapPerformanceTest::readJSONMapDirectPath);
        long time3 = test_Read(iterations,multiThread,()-> JSONMap.createWithJson(JSON_DATA),JSONMapPerformanceTest::readJSONMapSubObject);
        printTableRow(iterations, time1, time2, time3);
    }

    @SuppressWarnings("unchecked")
    private long test_Read(int iterations,boolean multiThread,
                           Supplier<Map<String, Object>> builder,
                           Function<Map<String, Object>,Integer> checker
    ) {
        Map<String, Object> response = builder.get();
        if(0 == checker.apply(response)){
            return -1;
        }
        // 正式测试
        long startTime = System.nanoTime();
        if(multiThread){
            new ArrayList<>(iterations).parallelStream().forEach(i -> {
                checker.apply(response);
            });
        }else{
            new ArrayList<>(iterations).stream().forEach(i -> {
                checker.apply(response);
            });
        }
        return (System.nanoTime() - startTime) /1000 ;
    }


    @SuppressWarnings("unchecked")
    private long test_ConstructAndRead(int iterations,boolean multiThread,
                                                     Supplier<Map<String, Object>> builder,
                                                     Function<Map<String, Object>,Integer> checker) {
        // 预热
        if(0 == checker.apply(builder.get())){
            return -1;
        }
        // 正式测试
        long startTime = System.nanoTime();
        if(multiThread){
            new ArrayList<>(iterations).parallelStream().forEach(i -> {
                checker.apply(builder.get());
            });
        }else{
            new ArrayList<>(iterations).stream().forEach(i -> {
                checker.apply(builder.get());
            });
        }
        return (System.nanoTime() - startTime) / 1_000;
    }

    // ==================== 场景 2：构造和读取多次 ====================
    private void testConstructAndReadPerformance(int iterations,boolean multiThread) {
        long time1 = test_ConstructAndRead(iterations,multiThread,()-> {
            try {
                return OBJECT_MAPPER.readValue(JSON_DATA, Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        },JSONMapPerformanceTest::readTraditionalWay);
        long time2 = test_ConstructAndRead(iterations,multiThread,()-> JSONMap.createWithJson(JSON_DATA),JSONMapPerformanceTest::readJSONMapDirectPath);
        long time3 = test_ConstructAndRead(iterations,multiThread,()-> JSONMap.createWithJson(JSON_DATA),JSONMapPerformanceTest::readJSONMapSubObject);
        printTableRow(iterations, time1, time2, time3);
    }
    // ==================== 读取方法 ====================

    @SuppressWarnings("unchecked")
    private static int readTraditionalWay(Map<String, Object> response) {
        Integer code = (Integer) response.get("code");
        if (code == null || code != 0) {
            return 1;
        }

        String orderId = null;
        String transactionId = null;
        Integer status = null;
        Integer amount = null;
        String openid = null;

        if (response.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null && data.containsKey("order")) {
                Map<String, Object> order = (Map<String, Object>) data.get("order");
                if (order != null) {
                    orderId = (String) order.get("orderId");
                    transactionId = (String) order.get("transactionId");
                    status = (Integer) order.get("status");
                    amount = (Integer) order.get("amount");
                }
            }
            if (data != null && data.containsKey("user")) {
                Map<String, Object> user = (Map<String, Object>) data.get("user");
                if (user != null) {
                    openid = (String) user.get("openid");
                }
            }
        }

        if (orderId != null && transactionId != null && status != null &&
            amount != null && openid != null) {
            // 模拟使用
            return 1;
        }
        return 0;
    }

    private static int readJSONMapDirectPath(Map<String, Object> para) {
            JSONMap response = (JSONMap)para;
        if (response.getInt("code") != 0) {
            return 1;
        }

        String orderId =  response.getStr("data.order.orderId");
        String transactionId = response.getStr("data.order.transactionId");
        Integer status = response.getInt("data.order.status");
        Integer amount =  response.getInt("data.order.amount");
        String openid =  response.getStr("data.user.openid");

        if (orderId != null && transactionId != null && status != null &&
            amount != null && openid != null) {
            return 1;
            // 模拟使用
        }
        return 0;
    }

    private static int readJSONMapSubObject(Map<String, Object> para) {
        JSONMap response = (JSONMap)para;
        if (response.getInt("code") != 0) {
            return 1;
        }

        JSONMap order = response.getMap("data.order");
        String orderId = order.getStr("orderId");
        String transactionId = order.getStr("transactionId");
        Integer status = order.getInt("status");
        Integer amount = order.getInt("amount");
        String openid = response.getStr("data.user.openid");

        if (orderId != null && transactionId != null && status != null &&
            amount != null && openid != null) {
            // 模拟使用
            return 1;
        }
        return 0;
    }

    // ==================== 辅助方法 ====================

    private void printTableRow(int iterations, long time1, long time2, long time3) {
        String perf2 = calculatePerformance(time1, time2);
        String perf3 = calculatePerformance(time1, time3);

        System.out.printf("│ %-15s │ %8d μs │ %8d μs %-12s │ %8d μs %-12s │%n",
            formatNumber(iterations), time1, time2, perf2, time3, perf3);
    }

    private String calculatePerformance(long baseline, long actual) {
        if (actual < baseline) {
            double improvement = (1 - (double) actual / baseline) * 100;
            return String.format("(快%.0f%%)", improvement);
        } else if (actual > baseline) {
            double degradation = ((double) actual / baseline - 1) * 100;
            return String.format("(慢%.0f%%)", degradation);
        } else {
            return "(相同)";
        }
    }

    private String formatNumber(int number) {
        if (number >= 100_000_000) {
            return (number / 100_000_000) + "亿";
        } else if (number >= 10_000) {
            return (number / 10_000) + "万";
        } else if (number >= 1000) {
            return (number / 1000) + "K";
        } else {
            return number + "次";
        }
    }
}
