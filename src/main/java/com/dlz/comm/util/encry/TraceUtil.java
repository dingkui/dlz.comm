package com.dlz.comm.util.encry;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class TraceUtil {
    private final static String KEY_TRACEID = "traceId";
    private TraceUtil(){
    }

    public static String getTraceid(){
        return MDC.get(KEY_TRACEID);
    }
    public static String makeTraceId(){
        String traceId = getTraceid();
        if(traceId ==null){
            traceId = UuidUtil.shortUuid();
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
