package com.dlz.comm.util;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p>Provides utilities for manipulating and examining
 * <code>Throwable</code> objects.</p>
 *
 * @since 1.0
 */
public class ExceptionTrace {
    /**
     * Caption  for labeling causative exception stack traces
     */
    private static final String CAUSE_CAPTION = "Caused by: ";
    /**
     * Caption for labeling suppressed exception stack traces
     */
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";
    private static final String LINE = "\n";
    private static final String TAB = "\t";

    final Throwable throwable;
    final boolean onlyShowAppLog;
    final Pattern marchPattern;

    final StringBuilder sb = new StringBuilder();
    private Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());

    public ExceptionTrace(final Throwable throwable, boolean onlyShowAppLog, Pattern marchPattern) {
        this.throwable = throwable;
        this.onlyShowAppLog = onlyShowAppLog;
        this.marchPattern = marchPattern;
    }

    public String getStackTrace(final String msg) {
        if(msg!=null){
            sb.append(msg).append(LINE);
        }
        dejaVu.add(throwable);
        // Print our stack trace
        sb.append(throwable).append(LINE);
        StackTraceElement[] trace = throwable.getStackTrace();
        for (StackTraceElement traceElement : trace)
            printTraceElement("", traceElement);

        // Print suppressed exceptions, if any
        for (Throwable se : throwable.getSuppressed())
            printEnclosedStackTrace(se, trace, "Suppressed: ", "\t");

        // Print cause, if any
        Throwable ourCause = throwable.getCause();
        if (ourCause != null)
            printEnclosedStackTrace(ourCause, trace, CAUSE_CAPTION, "");
        return sb.toString();
    }

    private void printEnclosedStackTrace(
            Throwable ourCause,
            StackTraceElement[] enclosingTrace,
            String caption,
            String prefix) {
        if (dejaVu.contains(ourCause)) {
            sb.append(TAB).append("[CIRCULAR REFERENCE:").append(ourCause).append("]").append(LINE);
        } else {
            dejaVu.add(ourCause);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = ourCause.getStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
                m--;
                n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // Print our stack trace
            sb.append(prefix).append(caption).append(ourCause).append(LINE);
            for (int i = 0; i <= m; i++)
                printTraceElement(prefix, trace[i]);

            if (framesInCommon != 0)
                sb.append(prefix).append(TAB).append("... ").append(framesInCommon).append(" more").append(LINE);

            // Print suppressed exceptions, if any
            for (Throwable se : ourCause.getSuppressed())
                printEnclosedStackTrace(se, trace, SUPPRESSED_CAPTION, prefix + TAB);

            // Print cause, if any
            Throwable ourCausei = ourCause.getCause();
            if (ourCausei != null)
                printEnclosedStackTrace(ourCausei, trace, CAUSE_CAPTION, prefix);
        }
    }

    public String getTraceElement(StackTraceElement trace) {
        String traceInfo = trace.toString();
        if (traceInfo.indexOf("CGLIB$") > -1) {
            return null;
        }
        if (!onlyShowAppLog||marchPattern==null) {
            return traceInfo;
        }
//        if(traceInfo.contains("org.springframework.aop")){
//            return;
//        }
//        if(traceInfo.contains("org.springframework.cglib")){
//            return;
//        }
//        if(traceInfo.contains("org.springframework.transaction")){
//            return;
//        }
//        if(traceInfo.contains("sun.reflect")){
//            return;
//        }
//        if(traceInfo.contains("java.lang.reflect")){
//            return;
//        }
//        if(traceInfo.contains("org.springframework.web")){
//            return;
//        }
//        if(traceInfo.contains("javax.servlet.http")){
//            return;
//        }
//        if(traceInfo.contains("io.undertow")){
//            return;
//        }
//        if(traceInfo.contains("java.util.concurrent")){
//            return;
//        }
        if (marchPattern.matcher(traceInfo).find()) {
            return traceInfo;
        }
        return  null;
    }
    /**
     * 过滤掉不需要打印的日志
     * @param trace
     */
    private void printTraceElement(String prefix, StackTraceElement trace) {
        String traceInfo = getTraceElement(trace);
        if (traceInfo != null) {
            sb.append(prefix).append(TAB).append("at ").append(traceInfo).append(LINE);
        }
    }
}
