package com.dlz.comm.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * <p>Provides utilities for manipulating and examining
 * <code>Throwable</code> objects.</p>
 *
 * @since 1.0
 */
public class ExceptionUtils {
    /**
     * Caption  for labeling causative exception stack traces
     */
    private static final String CAUSE_CAPTION = "Caused by: ";
    /**
     * Caption for labeling suppressed exception stack traces
     */
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";

    private String showPackage="com.dlz";

    Throwable throwable = null;
    final StringWriter sw = new StringWriter();
    final PrintWriter pw = new PrintWriter(sw, true);
    private Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());

    private void destroid() throws IOException {
        sw.close();
        pw.close();
        dejaVu.clear();
    }

    private ExceptionUtils(final Throwable throwable) {
        this.throwable = throwable;
    }

    public static String getStackTrace(final Throwable throwable,final String showPackage) {
        ExceptionUtils exceptionUtils = new ExceptionUtils(throwable);
        exceptionUtils.showPackage=showPackage;
        return exceptionUtils.getStackTrace();
    }
    public static String getStackTrace(final Throwable throwable) {
        return new ExceptionUtils(throwable).getStackTrace();
    }

    private String getStackTrace() {
        dejaVu.add(throwable);
        // Print our stack trace
        pw.println(throwable);
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
        return sw.getBuffer().toString();
    }

    private void printEnclosedStackTrace(
            Throwable ourCause,
            StackTraceElement[] enclosingTrace,
            String caption,
            String prefix) {
        if (dejaVu.contains(ourCause)) {
            pw.println("\t[CIRCULAR REFERENCE:" + ourCause + "]");
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
            pw.println(prefix + caption + ourCause);
            for (int i = 0; i <= m; i++)
//                pw.println(prefix + "\tat " + trace[i]);
                printTraceElement(prefix, trace[i]);
            if (framesInCommon != 0)
                pw.println(prefix + "\t... " + framesInCommon + " more");

            // Print suppressed exceptions, if any
            for (Throwable se : ourCause.getSuppressed())
                printEnclosedStackTrace(se, trace, SUPPRESSED_CAPTION, prefix + "\t");

            // Print cause, if any
            Throwable ourCausei = ourCause.getCause();
            if (ourCausei != null)
                printEnclosedStackTrace(ourCausei, trace, CAUSE_CAPTION, prefix);
        }
    }

    private void printTraceElement(String prefix, StackTraceElement trace) {
        String traceInfo = trace.toString();
        if (traceInfo.indexOf("CGLIB$") > -1) {
            return;
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
        if (traceInfo.indexOf(showPackage) == -1) {
            return;
        }
        pw.println(prefix + "\tat " + traceInfo);
    }
}
