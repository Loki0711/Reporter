package com.loki.testapp.network;

import com.loki.testapp.model.Forecast;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiClient {
    public static final String API_KEY = "current.json?key=0b891af40c534953b5e230726221402";

    private final ApiService apiService;

    public RestApiClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void fetchWeatherForecast(double longitude, double latitude, Callback callback) {
//        Call<Forecast> call = apiService.getWeatherForecast("0b891af40c534953b5e230726221402", String.valueOf(latitude+longitude));
        Call<Forecast> call = apiService.getWeatherForecast("0b891af40c534953b5e230726221402", String.valueOf(latitude+","+longitude));
        call.enqueue(callback);
    }
}