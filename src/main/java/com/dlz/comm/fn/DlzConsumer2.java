package com.dlz.comm.fn;


/**
 * 受检的 Consumer
 *
 * @author dk
 */
@FunctionalInterface
public interface DlzConsumer2<T1,T2> {
	void accept(T1 t,T2 t2);
}
