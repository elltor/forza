package org.forza.common.enums;

import lombok.AllArgsConstructor;

/**
 * @Author:  
 * @DateTime: 2020/4/17
 * @Description: TODO
 */
@AllArgsConstructor
public enum ConnectionTimeoutAction {
    NEW("new"),
    FIAL("fail");
    private String value;

    public String value() {
        return this.value;
    }
}
