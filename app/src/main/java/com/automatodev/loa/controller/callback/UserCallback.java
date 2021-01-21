package com.automatodev.loa.controller.callback;

import com.automatodev.loa.model.entity.UserEntity;

import retrofit2.Response;

//Interface de conexão com os metodos de resposta para extender contratos nas chamadas
public interface UserCallback {
    void onResponse(Response<UserEntity> response) throws Exception;
    void onFailure(Throwable t);
}
