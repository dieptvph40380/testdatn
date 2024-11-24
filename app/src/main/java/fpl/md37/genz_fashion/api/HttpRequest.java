package fpl.md37.genz_fashion.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpRequest {
    private ApiService requesInterface;
    public  HttpRequest(){
        requesInterface=new Retrofit.Builder().baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ApiService.class);
    }
    public ApiService callApi(){
        return requesInterface;
    }
}
