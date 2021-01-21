package com.automatodev.loa.controller.service;

import com.automatodev.loa.controller.callback.UserCallback;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.model.apiCall.UserAPI;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Classe de serviço que atende  as operações referentes ao usuario da aplicação
public class UserService {

    private Retrofit retrofit;
    private UserAPI userAPI;
    private OkHttpClient.Builder okHttpClient;

    //Construtor que cria um novo cliente http onde é configurado os times de conexão, assim como configura  um objeto retrofit e a interface de conexão.
    public UserService() {
        okHttpClient = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(35, TimeUnit.SECONDS)
                .readTimeout(35, TimeUnit.SECONDS);
        retrofit = new Retrofit.Builder().baseUrl(UrlConnection.urlBase).client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        userAPI = retrofit.create(UserAPI.class);

    }

    //Recupera um usuarjio pelo UUID dele
    public void getUserByUid(String uid, final UserCallback callback) {
        Call<UserEntity> call = userAPI.getByUid(uid);
        call.enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                callback.onFailure(t);
            }
        });

    }

    //Atualiza um usuario passando como body um objeto user, um id como parametro
    public void updateUser(long id, UserEntity user, UserCallback callback) {
        Call<UserEntity> call = userAPI.updateUser(id, user);
        call.enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Cria um novo usuario passando como body um objeto usuario na requisição
    public void postUser(UserEntity user, final UserCallback callback) {
        Call<UserEntity> call = userAPI.postUser(user);
        call.enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    //Recuperar um usuario pelo ID dele
    public void getUserById(Long id, final UserCallback callback) {
        Call<UserEntity> call = userAPI.getById(id);
        call.enqueue(new Callback<UserEntity>() {
            @Override
            public void onResponse(Call<UserEntity> call, Response<UserEntity> response) {
                try {
                    callback.onResponse(response);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UserEntity> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

}
