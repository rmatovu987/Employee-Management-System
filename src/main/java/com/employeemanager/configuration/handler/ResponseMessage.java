package com.employeemanager.configuration.handler;

public class ResponseMessage {

    public String message;

    public Object data;

    public ResponseMessage(String message) {
        super();
        this.message = message;
    }

    public ResponseMessage(String message, Object data) {
        super();
        this.message = message;
        this.data = data;
    }
}
