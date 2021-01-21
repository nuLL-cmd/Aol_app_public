package com.automatodev.loa.model.apiCall;

import com.automatodev.loa.model.entity.AdressEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AdressAPI {

    @GET("{cep}")
    Call<AdressEntity> getAdress(@Path("cep") int cep);

}
