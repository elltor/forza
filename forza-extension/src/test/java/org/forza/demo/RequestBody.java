package org.forza.demo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author:  
 * @DateTime: 2020/4/18
 * @Description: TODO
 */
@Data
public class RequestBody implements Serializable {

    private static final long serialVersionUID = 6622870532377526353L;

    private String name;
}
