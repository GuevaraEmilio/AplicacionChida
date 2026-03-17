package com.example.diaxl;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GNewsApi {
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("lang") String language,
        @Query("country") String country,
        @Query("token") String token
    );

    @GET("search")
    Call<NewsResponse> searchNews(
        @Query("q") String query,
        @Query("lang") String language,
        @Query("country") String country,
        @Query("token") String token
    );
}
