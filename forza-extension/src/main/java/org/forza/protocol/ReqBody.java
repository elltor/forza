package org.forza.protocol;


import lombok.Data;

import java.io.Serializable;

@Data
public class ReqBody implements Serializable {
    private String name;
    private Integer age;
}
