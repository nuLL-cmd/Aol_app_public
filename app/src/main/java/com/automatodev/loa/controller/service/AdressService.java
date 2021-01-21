package com.automatodev.loa.controller.service;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.LocationAdressCallback;
import com.automatodev.loa.model.apiCall.AdressAPI;
import com.automatodev.loa.model.entity.AdressEntity;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdressService {

   private String baseUrl = "https://cep.awesomeapi.com.br/json/";
   private Retrofit retrofit;
   private Activity context;
   private AdressAPI adress;
   private View view;


    public AdressService(Activity context){
        this.context = context;
        this.view = view;
        retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        adress = retrofit.create(AdressAPI.class);
    }

    public void getAdress(int cep, Dialog v, final LocationAdressCallback callback){
        TextView txtCity_locationCep = v.findViewById(R.id.txtCity_locationCep);
        LottieAnimationView lottieSuccess_location = v.findViewById(R.id.lottieSuccess_location);
        SpinKitView spinkProgressLocation = v.findViewById(R.id.spinkProgress_location);
        Call<AdressEntity> call = adress.getAdress(cep);
        try{
            call.enqueue(new Callback<AdressEntity> (){
                @Override
                public void onResponse(Call<AdressEntity> call, Response<AdressEntity> response){
                    if (response.isSuccessful() && response.body() != null){
                        callback.response(response.body());
                    }else{
                        try{
                            JSONObject object = new JSONObject(response.errorBody().string());
                            txtCity_locationCep.setText(object.getString("message"));
                            spinkProgressLocation.setVisibility(View.GONE);
                            lottieSuccess_location.setVisibility(View.VISIBLE);
                            lottieSuccess_location.setAnimation("error.json");
                            lottieSuccess_location.playAnimation();

                        }catch (IOException | JSONException e){
                            e.printStackTrace();
                            txtCity_locationCep.setText("Sua requisição tropeçou no caminho :(");
                            spinkProgressLocation.setVisibility(View.GONE);
                            lottieSuccess_location.setVisibility(View.VISIBLE);
                            lottieSuccess_location.setAnimation("error.json");
                            lottieSuccess_location.playAnimation();
                           Toast.makeText(context,"Ops: Houve um erro no retorno da requisição: \n"+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<AdressEntity> call, Throwable t){
                    t.printStackTrace();
                   Toast.makeText(context,"Ops: Houve um erro na chamada do serviço: \n"+t.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
           Toast.makeText(context,"Ops: Houve um erro ao processar sua requisição: \n"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

}
