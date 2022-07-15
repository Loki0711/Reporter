package com.loki.testapp.network;


import com.loki.testapp.model.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
  //  /resolve.json?url={url}&client_id={clientId}
    //@GET("current.json?key={key}&q={coord}")
  @GET("current.json")

    Call<Forecast> getWeatherForecast(
          @Query("key") String key,
          @Query("q") String longitude);
}