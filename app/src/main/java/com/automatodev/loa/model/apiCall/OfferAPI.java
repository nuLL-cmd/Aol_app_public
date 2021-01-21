package com.automatodev.loa.model.apiCall;

import com.automatodev.loa.model.entity.OfferEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OfferAPI {

    @GET("offers/user")
    Call<List<OfferEntity>> getAllByUser(@Query("id") long id);

    @GET("offers/item")
    Call<List<OfferEntity>> getAllByItem(@Query("id") long id, @Query("status") String status);

    @POST("offers")
    Call<Long> postOffer(@Body OfferEntity offerEntity);

    @DELETE("offers/{id}")
    Call<Long> deleteOffer(@Path("id") long id);

    @PUT("offers/{id}")
    Call<Long> updateOffer(@Path("id") long id, @Body OfferEntity offerEntity);


}
