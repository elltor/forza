package org.forza.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用RPC的接口
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ForzaAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableForza {

}
