package com.automatodev.loa.controller.callback;

import com.automatodev.loa.model.entity.CityEntity;

import java.util.List;

import retrofit2.Response;

//Interface de conexão com os metodos de resposta para extender contratos nas chamadas
public interface AdressCallback {

    void onResponse( Response<List<CityEntity>> response);
    void onFailure(Throwable t);
}
