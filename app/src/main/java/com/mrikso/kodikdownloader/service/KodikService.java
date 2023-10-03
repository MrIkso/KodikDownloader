package com.mrikso.kodikdownloader.service;

import com.mrikso.kodikdownloader.model.SearchResultModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface KodikService {
    @POST("/search?&with_seasons=true&with_episodes=true&sort=imdb_rating")
    Call<SearchResultModel> searchByTitle(@Query("token") String token, @Query ("title") String title);
    @POST("/search?&with_seasons=true&with_episodes=true&sort=imdb_rating")
    Call<SearchResultModel> searchByIMDbId(@Query("token") String token, @Query ("imdb_id") String id);
    @POST("/search?&with_seasons=true&with_episodes=true&sort=imdb_rating")
    Call<SearchResultModel> searchByKinopoiskId(@Query("token") String token, @Query ("kinopoisk_id") String id);
    @POST("/search?&with_seasons=true&with_episodes=true&sort=imdb_rating")
    Call<SearchResultModel> searchByShikimoriId(@Query("token") String token, @Query ("shikimori_id") String id);
}
