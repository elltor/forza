package com.forza.sample.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author:  
 * @DateTime: 2020/5/8
 * @Description: TODO
 */
@AllArgsConstructor
@Data
@ToString
public class SimpleResponseBody implements Serializable {
    private String data;
}
