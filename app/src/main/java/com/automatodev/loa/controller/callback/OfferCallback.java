package com.automatodev.loa.controller.callback;

import com.automatodev.loa.model.entity.OfferEntity;

import java.util.List;

import retrofit2.Response;

public interface OfferCallback {
    void onResponse(Response<List<OfferEntity>> response);
    void onResponseId(Response<Long> response);
    void onFailure(Throwable t);
}
