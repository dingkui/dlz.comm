package com.dlz.comm.fn;


/**
 * 受检的 Consumer
 *
 * @author dk
 */
@FunctionalInterface
public interface DlzFn2<T1,T2,R> {
	R apply(T1 t,T2 t2);
}
