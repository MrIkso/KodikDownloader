package com.mrikso.kodikdownloader.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mrikso.kodikdownloader.api.ApiClient;
import com.mrikso.kodikdownloader.service.KodikService;
import com.mrikso.kodikdownloader.model.SearchResultModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KodikRepository {
    public final String KODIK_TOKEN = "4492ae176f94d3103750b9443139fdc5";
    private static KodikService myInterface;
    private final MutableLiveData<SearchResultModel> listOfMovies = new MutableLiveData<>();

    private static KodikRepository newsRepository;

    public static KodikRepository getInstance() {
        if (newsRepository == null) {
            newsRepository = new KodikRepository();
        }

        return newsRepository;
    }

    public KodikRepository() {
        myInterface = ApiClient.getInstance().getKodikService();
    }

    public void getSearchByTitle(String name) {
        Call<SearchResultModel> listOfMovieOut = myInterface.searchByTitle(KODIK_TOKEN, name);
        listOfMovieOut.enqueue(
                new Callback<SearchResultModel>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<SearchResultModel> call,
                            @NonNull Response<SearchResultModel> response) {
                        //Log.d("KodikRepo", String.valueOf(response.body().getTotal()));
                        listOfMovies.postValue(response.body());
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<SearchResultModel> call, @NonNull Throwable t) {
                        listOfMovies.postValue(null);
                        t.printStackTrace();
                    }
                });
    }

    public LiveData<SearchResultModel> getResults() {
        return listOfMovies;
    }

    public void getSearchByIMDbId(String id) {
        Call<SearchResultModel> listOfMovieOut = myInterface.searchByIMDbId(KODIK_TOKEN, id);
        listOfMovieOut.enqueue(
                new Callback<SearchResultModel>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<SearchResultModel> call,
                            @NonNull Response<SearchResultModel> response) {
                   //     Log.d("KodikRepo", String.valueOf(response.body().getTotal()));
                        listOfMovies.postValue(response.body());
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<SearchResultModel> call, @NonNull Throwable t) {
                        listOfMovies.postValue(null);
                        t.printStackTrace();
                    }
                });
    }

    public void getSearchByKinopoiskId(String id) {
        Call<SearchResultModel> listOfMovieOut = myInterface.searchByKinopoiskId(KODIK_TOKEN, id);
        listOfMovieOut.enqueue(
                new Callback<SearchResultModel>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<SearchResultModel> call,
                            @NonNull Response<SearchResultModel> response) {
                        listOfMovies.postValue(response.body());
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<SearchResultModel> call, @NonNull Throwable t) {
                        listOfMovies.postValue(null);
                        t.printStackTrace();
                    }
                });
    }

    public void getSearchByShikimoriId(String id) {
        Call<SearchResultModel> listOfMovieOut = myInterface.searchByShikimoriId(KODIK_TOKEN, id);
        listOfMovieOut.enqueue(
                new Callback<SearchResultModel>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<SearchResultModel> call,
                            @NonNull Response<SearchResultModel> response) {
                     //   Log.d("KodikRepo", String.valueOf(response.body().getTotal()));
                        listOfMovies.postValue(response.body());
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<SearchResultModel> call, @NonNull Throwable t) {
                        listOfMovies.postValue(null);
                        t.printStackTrace();
                    }
                });
        ;
    }
}
