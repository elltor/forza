package org.forza.demo;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestBody implements Serializable {

    private static final long serialVersionUID = 6622870532377526353L;

    private String name;
}
