package com.example.diaxl;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface extractorAPI {
    @GET("extract")
    Call<textExtractor> extract(
            @Query("url") String url,
            @Query("api_token") String token
    );

    @GET("summarize")
    Call<textExtractor> summarize(
            @Query("url") String url,
            @Query("api_token") String token,
            @Query("sentences") int sentences
    );
}
