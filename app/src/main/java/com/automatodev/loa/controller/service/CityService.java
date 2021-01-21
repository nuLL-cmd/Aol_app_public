package com.automatodev.loa.controller.service;

import com.automatodev.loa.controller.callback.AdressCallback;
import com.automatodev.loa.model.entity.CityEntity;
import com.automatodev.loa.model.apiCall.CityAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Classe de serviço que busca os dados de cidades por uf diretamente do IBGE
public class CityService {

    private String baseUrl = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/";
    private Retrofit retrofit;

    //Construtor que inicia a interface de chamada a api do ibge
    public CityService() {
        retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }

    //Serviço que busca uma lista de cidades por id da UF
    public void getCities(int id, final AdressCallback callback ){
        CityAPI cityAPI = retrofit.create(CityAPI.class);
        Call<List<CityEntity>> call = cityAPI.getCities(id);
        call.enqueue(new Callback<List<CityEntity>>() {
            @Override
            public void onResponse(Call<List<CityEntity>> call, Response<List<CityEntity>> response) {
                try{
                    if (response.isSuccessful()){
                        callback.onResponse(response);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<CityEntity>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

}
