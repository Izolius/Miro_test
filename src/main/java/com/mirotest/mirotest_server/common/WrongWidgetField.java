package com.mirotest.mirotest_server.common;

public class WrongWidgetField extends RuntimeException {
    private String msg;
    public WrongWidgetField(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
