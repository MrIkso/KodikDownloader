package com.mrikso.kodikdownloader.api;

import com.mrikso.kodikdownloader.service.KodikService;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private final KodikService kodikService;
    public static ApiClient instance;

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
            return instance;
        }
        return instance;
    }

    public ApiClient() {

        kodikService =
                new Retrofit.Builder()
                        .baseUrl("https://kodikapi.com")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(KodikService.class);
    }

    public KodikService getKodikService() {
        return kodikService;
    }
    
    
    private OkHttpClient getClient() {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .followRedirects(true)
                        .callTimeout(20L, TimeUnit.SECONDS)
                        .build();
        return client;
    }
}
