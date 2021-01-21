package com.automatodev.loa.model.apiCall;

import com.automatodev.loa.model.entity.UserEntity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

//Interface de conexão que possui os metodos http usados nas chamadas.
public interface UserAPI {

    @GET("user/userId")
    Call<UserEntity> getById(@Query("id") long id);
    @GET("user/userUid")
    Call<UserEntity> getByUid(@Query("uid") String uid);
    @PUT("user/{id}")
    Call<UserEntity> updateUser(@Path("id") long id, @Body UserEntity user);
    @POST("user")
    Call<UserEntity> postUser(@Body UserEntity userEntity);

}
