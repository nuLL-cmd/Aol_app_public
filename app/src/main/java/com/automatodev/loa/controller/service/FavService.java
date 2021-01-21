package com.automatodev.loa.controller.service;

import com.automatodev.loa.controller.callback.FavCallback;
import com.automatodev.loa.model.entity.FavouriteEntity;
import com.automatodev.loa.model.apiCall.FavouriteAPI;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Classe de serviço dedicada aos favoritos
public class FavService {

    private Retrofit retrofit;
    private OkHttpClient.Builder status;
    private FavouriteAPI favAPI;

    //Construtor que inicia o httpCliente com os times da conexão
    public FavService() {
        status = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(35, TimeUnit.SECONDS)
                .readTimeout(35, TimeUnit.SECONDS);
        //Inicia um novo objeto retrofit e configura para ser usado o decorrer da classe
        retrofit = new Retrofit.Builder().baseUrl(UrlConnection.urlBase)
                .client(status.build())
                .addConverterFactory(GsonConverterFactory.create()).build();
        //Atribui a interface de conexão o objeto retrofit devidamento configurado
        favAPI = retrofit.create(FavouriteAPI.class);

    }

    //Serviço que busca um favorito tendo como base o ID dele
    public void getByidUser(long id, final FavCallback callback) {
        Call<List<FavouriteEntity>> call = favAPI.getByIdUser(id);
        call.enqueue(new Callback<List<FavouriteEntity>>() {
            @Override
            public void onResponse(Call<List<FavouriteEntity>> call, Response<List<FavouriteEntity>> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<FavouriteEntity>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Serviço que deleta um favorito tendo como base o id dele
    public void deleteById(long id, final FavCallback callback) {
        Call<Long> call = favAPI.deleteById(id);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                try {
                    callback.onResponseId(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Grava um novo favorito na tabela, tendo como parametro um objeto favorito
    public void postFavourites(FavouriteEntity fav, final FavCallback callback) {
        Call<Long> call = favAPI.postFavourite(fav);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                try {
                    callback.onResponseId(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Deleta todos os favoritos da tabela passando como parametro o id do usuario.
    public void deleteAll(Long id, FavCallback callback) {
        Call<Long> call = favAPI.deleteAll(id);
        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                try {
                    callback.onResponseId(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

}
