package com.automatodev.loa.controller.callback;

import com.automatodev.loa.model.entity.ItemEntity;

import java.util.List;

import retrofit2.Response;

//Interface de conexão com os metodos de resposta para extender contratos nas chamadas
public interface AnnouncementCallback {

    void onResponse(Response<ItemEntity> response);
    void onFailure(Throwable t);
    void onResponseAll(Response<List<ItemEntity>> response);

}
