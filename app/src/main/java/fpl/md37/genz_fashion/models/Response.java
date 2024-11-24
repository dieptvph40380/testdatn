package fpl.md37.genz_fashion.models;

import java.util.ArrayList;

public class Response<T> {
    private int status;
    private String message;
    private T data;

    // Getter và Setter
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
