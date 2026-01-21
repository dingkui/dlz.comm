package com.dlz.test.comm.map;

import com.dlz.comm.util.encry.TraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class CachePerformanceTest {

    public void testReadPer(Map<String, String> hashMap,int mapSize,int readSize) {
        List<String> keys = new ArrayList<>();
        List<Integer> indexs = new ArrayList<>();
        for (int i = 0; i < mapSize; i++) {
            keys.add(TraceUtil.generateShortUuid());
        }
        List<String> errKey = keys.stream().map(item->item.substring(1)+"_").collect(Collectors.toList());
        for (int i = 0; i < readSize; i++) {
            indexs.add(ThreadLocalRandom.current().nextInt(mapSize));
        }
        String name = hashMap.getClass().getSimpleName().substring(0,4)+"\t"+mapSize+"\t"+readSize;


        long startTime = System.nanoTime();
        keys.stream().forEach(item->hashMap.put(item, "value" + 1));
        long t1 = System.nanoTime()-startTime;

        startTime = System.nanoTime();
        indexs.stream().forEach(item->hashMap.get(errKey.get(item)));
        long t3 = System.nanoTime()-startTime;

        startTime = System.nanoTime();
        indexs.stream().forEach(item->hashMap.get(keys.get(item)));
        long t2 = System.nanoTime()-startTime;

        System.out.println(t1/mapSize+"\t"+t2/readSize+"\t"+t3/readSize+"\t:"+name);
    }
    public void testReadPer2(Map<String, String> hashMap,int mapSize,int readSize) {
        List<String> keys = new ArrayList<>();
        List<Integer> indexs = new ArrayList<>();
        for (int i = 0; i < mapSize; i++) {
            keys.add(TraceUtil.generateShortUuid());
        }
        List<String> errKey = keys.stream().map(item->item.substring(1)+"_").collect(Collectors.toList());
        for (int i = 0; i < readSize; i++) {
            indexs.add(ThreadLocalRandom.current().nextInt(mapSize));
        }
        String name = hashMap.getClass().getSimpleName().substring(0,4)+"\t"+mapSize+"\t"+readSize;


        long startTime = System.nanoTime();
        keys.parallelStream().forEach(item->hashMap.put(item, "value" + 1));
        long t1 = System.nanoTime()-startTime;

        startTime = System.nanoTime();
        indexs.parallelStream().forEach(item->hashMap.get(errKey.get(item)));
        long t3 = System.nanoTime()-startTime;

        startTime = System.nanoTime();
        indexs.parallelStream().forEach(item->hashMap.get(keys.get(item)));
        long t2 = System.nanoTime()-startTime;

        System.out.println(t1/mapSize+"\t"+t2/readSize+"\t"+t3/readSize+"\t:"+name);
    }

    @Test
    public void testHashMapReadPer() {
        testReadPer(new HashMap<>(),100,10000);
        testReadPer(new HashMap<>(),100,10000);
        testReadPer(new HashMap<>(),100,10000);
        testReadPer(new HashMap<>(),1000,100000);
        testReadPer(new HashMap<>(),1000,100000);
        testReadPer(new HashMap<>(),1000,100000);
        testReadPer(new ConcurrentHashMap<>(),100,10000);
        testReadPer(new ConcurrentHashMap<>(),100,10000);
        testReadPer(new ConcurrentHashMap<>(),100,10000);
        testReadPer(new ConcurrentHashMap<>(),1000,100000);
        testReadPer(new ConcurrentHashMap<>(),1000,100000);
        testReadPer(new ConcurrentHashMap<>(),1000,100000);
    }


    @Test
    public void testHashMapReadPer2() {
        testReadPer(new HashMap<>(),100,100000);
        testReadPer(new HashMap<>(),100,100000);
        testReadPer(new HashMap<>(),100,100000);
        testReadPer(new HashMap<>(),10000,100000);
        testReadPer(new HashMap<>(),10000,100000);
        testReadPer(new HashMap<>(),10000,100000);
        testReadPer2(new ConcurrentHashMap<>(),100,100000);
        testReadPer2(new ConcurrentHashMap<>(),100,100000);
        testReadPer2(new ConcurrentHashMap<>(),100,100000);
        testReadPer2(new ConcurrentHashMap<>(),10000,100000);
        testReadPer2(new ConcurrentHashMap<>(),10000,100000);
        testReadPer2(new ConcurrentHashMap<>(),10000,100000);
    }
}