package fpl.md37.genz_fashion.models;

import com.google.gson.annotations.SerializedName;

public class ResponseFavourite {
    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Favourite data;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Favourite getData() {
        return data;
    }
}
