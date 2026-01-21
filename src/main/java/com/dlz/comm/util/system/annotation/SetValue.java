package com.dlz.comm.util.system.annotation;

import java.lang.annotation.*;

/**
 * @author dk
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SetValue {
    String value() default "";
}
