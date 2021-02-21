package com.franco.rxjavainjava.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator2 {
    public static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private static final Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());

    private static final Retrofit retrofit = retrofitBuilder.build();

    private static final RequestApi2 requestApi = retrofit.create(RequestApi2.class);

    public static RequestApi2 getRequestApi2(){
        return requestApi;
    }
}
