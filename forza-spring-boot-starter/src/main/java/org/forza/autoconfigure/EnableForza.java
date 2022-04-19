package org.forza.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用RPC的接口
 *
 * @Author:  
 * @DateTime: 2020/5/8
 * @Description: TODO
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ForzaAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableForza {

}
