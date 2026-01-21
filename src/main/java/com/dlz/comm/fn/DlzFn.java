package com.dlz.comm.fn;

import java.io.Serializable;

/**
 * 函数式接口
 * 
 * 有参有返回 Function&lt;T, R&gt; -&gt; R apply(T t);
 * 
 * 其他常见函数式接口：
 * - 无参无返回 Runnable -&gt; void run();
 * - 无参有返回受检 Callable&lt;V&gt; -&gt; V call() throws Exception;
 * - 无参有返回 Supplier&lt;T&gt; -&gt; T get();
 * - 有2参无返回 MConsumer&lt;T1,T2&gt; -&gt; void accept(T1 t,T2 t2);
 * - 有2参有返回 MFunction2&lt;T1,T2,R&gt; -&gt; void accept(T1 t,T2 t2);
 * - 有参无返回 Consumer&lt;T&gt; -&gt; void accept(T t);
 * - 比较 Comparator&lt;T&gt; -&gt; int compare(T o1, T o2);
 *
 * @param <T> 输入参数类型
 * @param <R> 返回值类型
 * 
 * @author dlz
 * @since 2023
 */
@FunctionalInterface
public interface DlzFn<T, R> extends Serializable {
    /**
     * 应用函数
     * 
     * @param t 输入参数
     * @return 返回值
     */
    R apply(T t);
}