package com.wtz.tools.test.aac.repository.remote;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface NetApi {

    @GET
    Observable<Response<String>> get(@Url String url);

    @GET
    Observable<Response<String>> get(@Url String url, @QueryMap Map<String, Object> params);

    @GET("http://pv.sohu.com/cityjson?ie=utf-8")
    Observable<Response<String>> getOuterNetIp();

}
