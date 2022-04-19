package org.forza.sample;

import lombok.Data;

/**
 * wrapper response data.
 *
 * @author liuqichun
 */
@Data
public class Result {
    private int code;
    private String msg;
    private Object data;

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success(Object data) {
        return new Result(200, "ok", data);
    }

    public static Result error(Object data) {
        return new Result(400, "error", data);
    }

}
