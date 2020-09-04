package com.openwes.web;

import com.openwes.core.utils.Utils;

/**
 *
 * @author xuanloc0511@gmail.com
 * @param <T>
 */
public class ResponseMessage<T extends Object> {

    public final static ResponseMessage error(int code, String message) {
        return new ResponseMessage()
                .setCode(code)
                .setMessage(message)
                .setData(null);
    }

    public final static ResponseMessage success(int code, String message) {
        return success(code, message, null);
    }

    public final static ResponseMessage success(int code, String message, Object data) {
        return new ResponseMessage()
                .setCode(code)
                .setMessage(message)
                .setData(data);
    }

    private int code;
    private String message;
    private T data;

    public int getCode() {
        return code;
    }

    public <E extends ResponseMessage> E setCode(int code) {
        this.code = code;
        return (E) this;
    }

    public String getMessage() {
        return message;
    }

    public <E extends ResponseMessage> E setMessage(String message) {
        this.message = message;
        return (E) this;
    }

    public T getData() {
        return data;
    }

    public <E extends ResponseMessage> E setData(T data) {
        this.data = data;
        return (E) this;
    }

    public String json() {
        return Utils.marshal(this);
    }
}
