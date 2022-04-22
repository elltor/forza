package com.forza.sample.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class GoodsResponseBody implements Serializable {
    private List<Goods> goodsList;
}
