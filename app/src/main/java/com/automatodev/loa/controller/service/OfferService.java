package com.automatodev.loa.controller.service;

import com.automatodev.loa.controller.callback.OfferCallback;
import com.automatodev.loa.model.apiCall.OfferAPI;
import com.automatodev.loa.model.entity.OfferEntity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.internal.EverythingIsNonNull;

public class OfferService {

    Retrofit retrofit;
    OfferAPI offerAPI;
    OkHttpClient.Builder client;

    public OfferService(){
         client = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2,TimeUnit.SECONDS)
                .readTimeout(35,TimeUnit.SECONDS)
                .writeTimeout(35,TimeUnit.SECONDS);

         retrofit = new Retrofit.Builder().baseUrl(UrlConnection.urlBase)
                 .addConverterFactory(GsonConverterFactory.create())
                 .client(client.build())
                 .build();

         offerAPI = retrofit.create(OfferAPI.class);
    }

    @EverythingIsNonNull
    public void getAllByUser(long id, OfferCallback callback){
        Call<List<OfferEntity>> call = offerAPI.getAllByUser(id);
        call.enqueue(new Callback<List<OfferEntity>>() {
            @Override
            public void onResponse(Call<List<OfferEntity>> call, Response<List<OfferEntity>>response) {
                try{
                    callback.onResponse(response);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<OfferEntity>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    @EverythingIsNonNull
    public void getAllByItem(long id, String status, OfferCallback callback){
        Call<List<OfferEntity>> call = offerAPI.getAllByItem(id, status);
        call.enqueue(new Callback<List<OfferEntity>>() {
            @Override
            public void onResponse(Call<List<OfferEntity>> call, Response<List<OfferEntity>> response) {
                try{
                    callback.onResponse(response);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<OfferEntity>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    @EverythingIsNonNull
    public void postOffer(OfferEntity offerEntity, OfferCallback callback){
        Call<Long> call = offerAPI.postOffer(offerEntity);
        call.enqueue(new Callback<Long>(){
            @Override
            public void onResponse(Call<Long> call, Response<Long> response){
                try{
                    callback.onResponseId(response);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    @EverythingIsNonNull
    public void deleteOffer(long id, OfferCallback callback){
        Call<Long> call = offerAPI.deleteOffer(id);
        call.enqueue(new Callback<Long>(){
            @Override
            public void onResponse(Call<Long> call, Response<Long> response){
                callback.onResponseId(response);
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t){
                callback.onFailure(t);
            }
        });
    }

    @EverythingIsNonNull
    public void updateOffer(long id, OfferEntity offerEntity, OfferCallback callback){
        Call<Long> call = offerAPI.updateOffer(id,offerEntity);
        call.enqueue(new Callback<Long>(){
            @Override
            public void onResponse(Call<Long> call, Response<Long> response){
                try{
                    callback.onResponseId(response);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(Call<Long> call, Throwable t){
                callback.onFailure(t);
            }
        });
    }


}
