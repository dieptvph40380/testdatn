package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

public class ResponseCart {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private CartData data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public CartData getData() {
        return data;
    }
}
