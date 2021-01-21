package com.automatodev.loa.model.apiCall;

import com.automatodev.loa.model.entity.FavouriteEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

//Interface de conexão que possui os metodos http usados nas chamadas.
public interface FavouriteAPI {

    @GET("favourites")
    Call<List<FavouriteEntity>> getAll();

    @GET("favourites/{id}")
    Call<List<FavouriteEntity>> getByIdUser(@Path("id") long id);

    @DELETE("favourites/{id}")
    Call<Long>deleteById(@Path("id") long id);

    @DELETE("favourites")
    Call<Long> deleteAll(@Query("id") Long id);

    @POST("favourites")
    Call<Long> postFavourite(@Body FavouriteEntity fav);

}
