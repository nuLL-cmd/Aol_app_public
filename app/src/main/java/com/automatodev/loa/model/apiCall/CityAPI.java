package com.automatodev.loa.model.apiCall;

import com.automatodev.loa.model.entity.CityEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

//Interface de conexão que possui os metodos http usados nas chamadas.
public interface CityAPI {

    @GET("{id}/municipios")
    Call<List<CityEntity>> getCities(@Path("id") int id);

}
