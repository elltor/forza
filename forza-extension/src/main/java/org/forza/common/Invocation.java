package org.forza.common;

import lombok.Data;

@Data
public class Invocation<T> {
    private String className;
    private T data;


}
