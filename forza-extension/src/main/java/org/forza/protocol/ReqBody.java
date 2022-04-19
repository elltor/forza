package org.forza.protocol;


import lombok.Data;

import java.io.Serializable;

/**
 * @Author:  
 * @DateTime: 2020/4/20
 * @Description: TODO
 */
@Data
public class ReqBody implements Serializable {
    private String name;
    private Integer age;
}
