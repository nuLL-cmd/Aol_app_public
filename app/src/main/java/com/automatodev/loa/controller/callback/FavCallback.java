package com.automatodev.loa.controller.callback;

import com.automatodev.loa.model.entity.FavouriteEntity;

import java.util.List;

import retrofit2.Response;

//Interface de conexão com os metodos de resposta para extender contratos nas chamadas
public interface FavCallback {
    void onResponse(Response<List<FavouriteEntity>> response);
    void onFailure(Throwable t);
    void onResponseId(Response<Long> response);
}
