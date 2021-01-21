package com.automatodev.loa.model.apiCall;

import com.automatodev.loa.model.entity.ItemEntity;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

//Interface de conexão que possui os metodos http usados nas chamadas.
public interface ItemAPI {

    @GET("announcement")
    Call<List<ItemEntity>> getAllAnnouncement(@Query("uf") String uf, @Query("city") String city);

    @GET("announcement/{id}")
    Call<ItemEntity> getById(@Path("id") long id);

    @POST("announcement")
    Call<ItemEntity> postAnnouncement(@Body ItemEntity itemEntity);

    @PUT("announcement/{id}")
    Call<ItemEntity> putAnnouncement(@Path("id") long id, @Body ItemEntity itemEntity);

    @DELETE("announcement/{id}")
    Call<ItemEntity> deleteAnnouncement(@Path("id")long id);

    @GET("announcement/filter")
    Call<List<ItemEntity>> getResultSet(@QueryMap(encoded = true) Map<String, String> query);

}
