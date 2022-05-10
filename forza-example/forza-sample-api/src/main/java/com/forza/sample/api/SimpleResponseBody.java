package com.forza.sample.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@Data
@ToString
public class SimpleResponseBody implements Serializable {
    private String data;
}
