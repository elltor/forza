package com.forza.sample.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Data
@ToString
public class SimpleRequestBody implements Serializable {
    private String name;
    private int age;
    private long phone;
}
