package com.example.iot_mobile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {

    @POST
    Call<Void> sendCommand(
            @Url String url,
            @Header("Authorization") String authToken,
            @Body CommandRequest body
    );
}
