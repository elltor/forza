package com.forza.sample.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ToString
public class Goods implements Serializable {
    private int id;
    private String name;
    private String category;
}
