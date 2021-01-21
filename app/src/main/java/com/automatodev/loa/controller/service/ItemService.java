package com.automatodev.loa.controller.service;

import com.automatodev.loa.controller.callback.AnnouncementCallback;
import com.automatodev.loa.model.entity.ItemEntity;
import com.automatodev.loa.model.apiCall.ItemAPI;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Classe de serviço que atende as operações dos anúncios
public class ItemService {

    private ItemAPI itemAPI;
    private Retrofit retrofit;
    private OkHttpClient.Builder okHttpClient;

    //Construtor que cria um novo cliente http onde é configurado os times de conexão, assim como configura  um objeto retrofit e a interface de conexão.
    public ItemService() {
        okHttpClient = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(35, TimeUnit.SECONDS)
                .writeTimeout(35, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder().baseUrl(UrlConnection.urlBase).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient.build()).build();
        itemAPI = retrofit.create(ItemAPI.class);
    }

    //Obtem um item com base no seu id
    public void getAnnouncementById(long id, AnnouncementCallback callback) {
        Call<ItemEntity> call = itemAPI.getById(id);
        call.enqueue(new Callback<ItemEntity>() {
            @Override
            public void onResponse(Call<ItemEntity> call, Response<ItemEntity> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ItemEntity> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Obtem todos os itens passando como parametro a uf e cidade escolhida no app
    public void getAnnouncementAll(String uf, String city, final AnnouncementCallback callback) {
        Call<List<ItemEntity>> call = itemAPI.getAllAnnouncement(uf, city);
        call.enqueue(new Callback<List<ItemEntity>>() {
            @Override
            public void onResponse(Call<List<ItemEntity>> call, Response<List<ItemEntity>> response) {
                try {
                    callback.onResponseAll(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<List<ItemEntity>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Deleta um anúncio com base no id
    public void deleteAnnouncement(long id, AnnouncementCallback callback) {
        Call<ItemEntity> call = itemAPI.deleteAnnouncement(id);
        call.enqueue(new Callback<ItemEntity>() {
            @Override
            public void onResponse(Call<ItemEntity> call, Response<ItemEntity> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ItemEntity> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Grava um novo anúncio, passando um item como body da requisição
    public void postAnnouncement(ItemEntity item, final AnnouncementCallback callback) {
        Call<ItemEntity> call = itemAPI.postAnnouncement(item);
        call.enqueue(new Callback<ItemEntity>() {
            @Override
            public void onResponse(Call<ItemEntity> call, Response<ItemEntity> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ItemEntity> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Atualiza um item passando no body da requisição um novo item, e um id como parametro
    public void updateAnnouncement(long id, ItemEntity item, final AnnouncementCallback callback) {
        Call<ItemEntity> call = itemAPI.putAnnouncement(id, item);
        call.enqueue(new Callback<ItemEntity>() {
            @Override
            public void onResponse(Call<ItemEntity> call, Response<ItemEntity> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ItemEntity> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Serviço de filtragem basica que trara todos os itens com base nos parametros do HashMap
    public void getResultSet(Map<String, String> query, AnnouncementCallback callback) {
        Call<List<ItemEntity>> call = itemAPI.getResultSet(query);
        call.enqueue(new Callback<List<ItemEntity>>() {
            @Override
            public void onResponse(Call<List<ItemEntity>> call, Response<List<ItemEntity>> response) {
                try {
                    callback.onResponseAll(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<ItemEntity>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

}
