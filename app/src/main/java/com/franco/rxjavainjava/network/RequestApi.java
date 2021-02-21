package com.franco.rxjavainjava.network;

import com.franco.rxjavainjava.domain.Model.Comment;
import com.franco.rxjavainjava.domain.Model.Post;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.rxjava3.core.Observable;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RequestApi {

    @GET("todos/1")
    Observable<ResponseBody> makeObservableQuery();

    @GET("todos/1")
    Flowable<ResponseBody> makeQuery();


}