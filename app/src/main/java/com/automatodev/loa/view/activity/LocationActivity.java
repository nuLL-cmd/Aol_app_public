package com.automatodev.loa.view.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.AdressCallback;
import com.automatodev.loa.controller.service.AdressService;
import com.automatodev.loa.controller.service.CityService;
import com.automatodev.loa.model.entity.CityEntity;
import com.automatodev.loa.model.entity.CountryEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.view.extras.RequestPermission;
import com.github.ybq.android.spinkit.SpinKitView;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class LocationActivity extends IntroActivity {

    private RequestPermission requestPermission;
    private String[] permissionsList = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};
    private EditText edtCep_locationCep;
    private TextView txtCity_locationCep;
    private Button btnSearch_locationCep;
    private Button btnSave_locationCep;
    private SpinKitView spinkProgress_location;
    private LottieAnimationView lottieSuccess_location;
    private boolean agree = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestPermission = new RequestPermission(this, permissionsList);
        requestPermission.requestInitialPermission();


        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.layout_five)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.layout_one)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.layout_two)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.layout_tree)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.layout_four)
                .canGoForward(false)
                .build()
        );

        setButtonBackVisible(false);
        setButtonNextVisible(true);

    }
    
    public void agreeTerms (View view){
       if (!agree){
           agree = true;
           Toast.makeText(this, "Legal, agora você pode prosseguir!", Toast.LENGTH_SHORT).show();
       }
       else{
           agree = false;
           Toast.makeText(this, "Você precisa concordar com os termos e condições do app!", Toast.LENGTH_SHORT).show();
       }
    }

    public void setLocationManual(View view) {
        if (!agree){
            alertNoAgreeTerms();
            return;
        }
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_dialog_location);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Spinner spinnerUF_location = dialog.findViewById(R.id.spinnerUF_location);
        SearchableSpinner spinnerCity_location = dialog.findViewById(R.id.spinnerCity_location);
        Button btnSave_location = dialog.findViewById(R.id.btnSave_location);
        RelativeLayout relativeProgressCity_location = dialog.findViewById(R.id.relativeProgressCity_location);
        UserEntity u = new UserEntity();
        inflateSpinnerUf(spinnerUF_location, spinnerCity_location, relativeProgressCity_location, u);
        btnSave_location.setOnClickListener(view1 -> {
            if(u.getUf() == null || u.getCity() == null){
                Toast.makeText(LocationActivity.this,
                        "Hum parece que você não selecionou um estado / cidade \nVocê pode tentar o outro metodo!", Toast.LENGTH_LONG).show();
            }else{
                Map<String, String> map = new HashMap<>();
                map.put("uf",u.getUf());
                map.put("city",u.getCity());
                dialog.dismiss();
                new Task().execute(map);
            }
        });
        dialog.show();
    }

    public void inflateSpinnerUf(Spinner spinnerUF_location, SearchableSpinner spinnerCity_location, RelativeLayout relativeProgressCity_location, UserEntity u) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this
                , R.array.states, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUF_location.setAdapter(adapter);
        spinnerUF_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                u.setUf(CountryEntity.populateUf().get(position).getSigla());
                getCityCloud(CountryEntity.populateUf().get(position).getId(), spinnerCity_location, relativeProgressCity_location, u);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }



    public void getCityCloud(int position, SearchableSpinner spinnerCity_location, RelativeLayout relativeProgressCity_location, UserEntity u) {
        relativeProgressCity_location.setVisibility(View.VISIBLE);
        CityService adress = new CityService();
        adress.getCities(position, new AdressCallback() {
            @Override
            public void onResponse(Response<List<CityEntity>> response) {
                inflateSpinnerCity(response.body(), spinnerCity_location, relativeProgressCity_location, u);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    public void inflateSpinnerCity(List<CityEntity> cities, SearchableSpinner spinnerCity_location, RelativeLayout relativeProgressCity_location, UserEntity u) {
        relativeProgressCity_location.setVisibility(View.GONE);
        List<String> cidades = new ArrayList<>();
        spinnerCity_location.setTitle("Selecione a cidade");
        cities.forEach(c -> cidades.add(c.getNome()));
        spinnerCity_location.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidades));
        spinnerCity_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                u.setCity(cities.get(position).getNome());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void setLocationCEP(View view) {
        if (!agree){
            alertNoAgreeTerms();
            return;
        }
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bottom_location_cep);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        UserEntity u = new UserEntity();
        ImageView imgResource_locationCep = dialog.findViewById(R.id.imgResource_locationCep);
        spinkProgress_location = dialog.findViewById(R.id.spinkProgress_location);
        lottieSuccess_location = dialog.findViewById(R.id.lottieSuccess_location);
        btnSearch_locationCep = dialog.findViewById(R.id.btnSearch_locationCep);
        btnSave_locationCep = dialog.findViewById(R.id.btnSave_locationCep);
        edtCep_locationCep = dialog.findViewById(R.id.edtCep_locationCep);
        txtCity_locationCep = dialog.findViewById(R.id.txtCity_locationCep);
        edtCep_locationCep.requestFocus();
        dialog.show();

        btnSearch_locationCep.setOnClickListener(v2 -> {
            if (edtCep_locationCep.getText().toString().trim().isEmpty())
                edtCep_locationCep.setError("Deve ser informado um CEP");
            else {
                imgResource_locationCep.setVisibility(View.GONE);
                spinkProgress_location.setVisibility(View.VISIBLE);
                lottieSuccess_location.setVisibility(View.GONE);
                getLocationCEP(Integer.parseInt(edtCep_locationCep.getText().toString().trim()), dialog,  u);
            }
        });
        btnSave_locationCep.setOnClickListener(v2 -> {
            if(u.getUf() == null || u.getCity() == null){
                Toast.makeText(LocationActivity.this,
                        "Hum parece que você não selecionou um estado / cidade \nVocê pode tentar o outro metodo!", Toast.LENGTH_LONG).show();
            }else{
                Map<String, String> map = new HashMap<>();
                map.put("uf",u.getUf());
                map.put("city",u.getCity());
                dialog.dismiss();
                new Task().execute(map);
            }

        });
    }

    public void getLocationCEP(int cep, Dialog dialog, UserEntity u){
        AdressService lService = new AdressService(this);
        lService.getAdress(cep, dialog, adressEntity -> {
            txtCity_locationCep.setText("");
            txtCity_locationCep.setText(adressEntity.getCity() + " - " + adressEntity.getState());
            u.setUf(adressEntity.getState());
            u.setCity(adressEntity.getCity());
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        spinkProgress_location.post(() -> {
                            spinkProgress_location.setVisibility(View.GONE);
                            lottieSuccess_location.setAnimation("success.json");
                            lottieSuccess_location.setVisibility(View.VISIBLE);
                            lottieSuccess_location.playAnimation();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        });
    }

    public void alertNoAgreeTerms(){
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View v = getLayoutInflater().inflate(R.layout.layout_dialog, null);
        TextView txtTitle = v.findViewById(R.id.txtTitle_dialog);
        TextView txtDialog_dialog = v.findViewById(R.id.txtDialog_dialog);
        txtTitle.setText(R.string.str_no_terms_title);
        txtDialog_dialog.setText(R.string.str_no_terms_message);
        Button btnOk_dialog = v.findViewById(R.id.btnOk_dialog);
        Button btnNo_dialog = v.findViewById(R.id.btnNo_dialog);
        btnOk_dialog.setText(R.string.str_no_tems_understand);
        btnNo_dialog.setText(R.string.str_no_terms_exit);
        btnOk_dialog.setOnClickListener(v1 -> alert.dismiss());
        btnNo_dialog.setOnClickListener(v2 -> finishAffinity());
        alert.setCancelable(false);
        alert.setView(v);
        alert.show();
    }

    public class Task extends AsyncTask<Map<String, String>, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialog dialog = new ProgressDialog(LocationActivity.this);
            dialog.setTitle("Aguarde...");
            dialog.setMessage("Salvando configurações iniciais");
            dialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            SharedPreferences prefs = getSharedPreferences("initData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("uf", maps[0].get("uf"));
            editor.putString("city", maps[0].get("city"));
            editor.putBoolean("havConfig",true);
            editor.putBoolean("termsAgree",true);
            editor.apply();

            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent i = new Intent(LocationActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

}