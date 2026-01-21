package com.dlz.comm.util.encry;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.UUID;

@Slf4j
public class TraceUtil {
    private final static String KEY_TRACEID = "traceId";
    private TraceUtil(){
    }
    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }
    public static String getTraceid(){
        return MDC.get(KEY_TRACEID);
    }
    public static String makeTraceId(){
        String traceId = getTraceid();
        if(traceId ==null){
            traceId = generateShortUuid();
        }else{
            log.warn("traceId:{} is multiSet in this thread, skip this make!",KEY_TRACEID,traceId);
        }
        return traceId;
    }
    public static String setTraceId(){
        return setTraceId(null);
    }
    public static String setTraceId(String traceId){
        if(traceId != null){
            String oldtraceId = MDC.get(KEY_TRACEID);
            if(oldtraceId != null){
                if(oldtraceId.equals(traceId)){
                    log.warn("traceId:{} has set in this same thread!",traceId);
                    return traceId;
                }
                log.error("traceId:{} has set in this thread, but you will set a new traceId:{}!",oldtraceId,traceId);
            }
        }else{
            traceId = makeTraceId();
        }
        MDC.put(KEY_TRACEID, traceId);
        return traceId;
    }
    public static void clearTraceId(){
        String traceId = getTraceid();
        if(traceId ==null){
            log.warn("traceId:{} is null!",KEY_TRACEID);
        }else{
            MDC.remove(KEY_TRACEID);
        }
    }
}
