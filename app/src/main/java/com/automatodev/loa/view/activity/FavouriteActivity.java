package com.automatodev.loa.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.FavCallback;
import com.automatodev.loa.controller.callback.UserCallback;
import com.automatodev.loa.controller.service.FavService;
import com.automatodev.loa.controller.service.UserService;
import com.automatodev.loa.databinding.ActivityFavouriteBinding;
import com.automatodev.loa.model.entity.FavouriteEntity;
import com.automatodev.loa.model.entity.ItemEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.view.adapter.ItemAdapter;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.extras.PreferencesApplication;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;

public class FavouriteActivity extends AppCompatActivity {

    ActivityFavouriteBinding binding;
    public static boolean update;
    private ItemAdapter itemAdapter;
    private UserEntity userEntity;
    private List<FavouriteEntity> favourites;
    private List<ItemEntity> items;
    private ComponentListener componentListener;
    public static boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        View viewFavourite = binding.getRoot();
        setContentView(viewFavourite);

        items = new ArrayList<>();
        componentListener = new ComponentListener(this);
        favourites = new ArrayList<>();
        binding.swipeRefreshFav.setOnRefreshListener(() -> {
            if (userEntity.getIdUser() != null) {
                getAllFav(userEntity.getIdUser());
            }

        });
        binding.swipeRefreshFav.setColorSchemeResources(R.color.colorPrimaryDark2);
        loadUser(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

    public void deleteAllFav(View view) {
        if (items.size() != 0) {
            final AlertDialog alerta = new AlertDialog.Builder(this).create();
            final View v = getLayoutInflater().inflate(R.layout.layout_dialog, null);
            Button btnOk_dialog = v.findViewById(R.id.btnOk_dialog);
            Button btnNo_dialog = v.findViewById(R.id.btnNo_dialog);
            btnOk_dialog.setText(R.string.txtBtnYesDialog);
            btnNo_dialog.setText(R.string.txtBtnCancelDialog);
            TextView txtTitle_dialog = v.findViewById(R.id.txtTitle_dialog);
            TextView txtDialog_dialog = v.findViewById(R.id.txtDialog_dialog);
            txtTitle_dialog.setText(R.string.titleDialogAllFav);
            txtDialog_dialog.setText(R.string.textDialogAllFav);
            btnOk_dialog.setOnClickListener(view2 -> {
                deleteAll();
                alerta.dismiss();
            });
            btnNo_dialog.setOnClickListener(view1 -> alerta.dismiss());
            alerta.setView(v);
            alerta.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alerta.show();

        }
    }

    public void getAllFav(Long id) {
        componentListener.fadeComponents(binding.recyclerItensFav, "alpha", 1f, 0f, 250, false);
        FavService favService = new FavService();
        if (id != null) {
            binding.swipeRefreshFav.setRefreshing(true);
            try {
                favService.getByidUser(id, new FavCallback() {
                    @Override
                    public void onResponse(Response<List<FavouriteEntity>> response) {
                        if (response.isSuccessful()) {
                            binding.imgResourcesFav.setImageResource(R.drawable.ic_undraw_surfer_m6jb);
                            binding.txtTitleResourcesFav.setText(getResources().getString(R.string.titleNoContent));
                            binding.txtMessageResourcesFav.setText(getResources().getString(R.string.messageNoContentFav));
                            items.clear();
                            favourites.clear();
                            favourites = response.body();
                            favourites.forEach(f -> items.add(f.getAnnouncementEntity()));
                            showDataFav(items);
                            binding.swipeRefreshFav.setRefreshing(false);
                        }else{
                            try{
                                JSONObject objectError = new JSONObject(response.errorBody().string());
                                Snackbar.make(binding.relativeParentFav,"Ops! "+objectError.getString("title"),Snackbar.LENGTH_LONG).show();

                            }catch(IOException | JSONException e){
                                Snackbar.make(binding.relativeParentFav,"Message "+e.getMessage(),Snackbar.LENGTH_LONG).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (itemAdapter != null) {
                            componentListener.fadeComponents(binding.recyclerItensFav, "alpha", 1f, 0f, 250, false);
                            items.clear();
                            itemAdapter.notifyDataSetChanged();
                        }
                        binding.imgResourcesFav.setImageResource(R.drawable.ic_undraw_notify_re_65on);
                        binding.txtTitleResourcesFav.setText(getResources().getString(R.string.titleErrorConnection));
                        binding.txtMessageResourcesFav.setText(getResources().getString(R.string.messageErrorConnectionFav));
                        componentListener.fadeComponents(binding.relativeResourcesFav, "alpha", 0f, 1f, 250, false);
                        binding.relativeResourcesFav.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onResponseId(Response<Long> response) {
                    }
                });
            } catch (Exception e) {
                Log.e("logx", "EDeleteAll: " + e.getMessage());
                Snackbar.make(binding.relativeParentFav,"Tivemos um problema ao recuperar seus favoritos " +
                        "\nPor favor reinicie o app e tente novamente",Snackbar.LENGTH_LONG).show();
            }

        }
    }
    public void deleteSingle(long posItem, int posList) {
        itemAdapter.notifyItemRemoved(posList);
        favourites.remove(posList);
        items.remove(posList);
        FavService favService = new FavService();
        try {
            favService.deleteById(posItem, new FavCallback() {
                @Override
                public void onResponse(Response<List<FavouriteEntity>> response) {
                }

                @Override
                public void onFailure(Throwable t) {
                    Snackbar.make(binding.relativeParentFav,"Ops! Não foi possivel processar sua requisiçaõ\nVerifique sua conexão e tente novamente.",Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onResponseId(Response<Long> response) {
                    if (!response.isSuccessful()) {
                        try{
                            JSONObject objectError = new JSONObject(response.errorBody().string());
                            Snackbar.make(binding.relativeParentFav,"Ops! "+objectError.getString("title"),Snackbar.LENGTH_LONG).show();

                        }catch(IOException | JSONException e){
                            Snackbar.make(binding.relativeParentFav,"Message "+e.getMessage(),Snackbar.LENGTH_LONG).show();

                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("logx", "EDeleteAll: " + e.getMessage());
            Snackbar.make(binding.relativeParentFav,"Tivemos um problema ao processar sua solicitação. " +
                    "\nPor favor reinicie o app e tente novamente",Snackbar.LENGTH_LONG).show();
        }

    }

    public void deleteAll() {
        itemAdapter.notifyItemRangeRemoved(0, items.size());
        items.clear();
        FavService favService = new FavService();
        try {
            favService.deleteAll(userEntity.getIdUser(), new FavCallback() {
                @Override
                public void onResponseId(Response<Long> response) {
                    if (!response.isSuccessful()) {
                        try{
                            JSONObject objectError = new JSONObject(response.errorBody().string());
                            Snackbar.make(binding.relativeParentFav,"Ops! "+objectError.getString("title"),Snackbar.LENGTH_LONG).show();

                        }catch(IOException | JSONException e){
                            Snackbar.make(binding.relativeParentFav,"Message "+e.getMessage(),Snackbar.LENGTH_LONG).show();

                        }
                    }
                }
                @Override
                public void onResponse(Response<List<FavouriteEntity>> response) {
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("logx", "Error: " + t.getMessage());
                    Snackbar.make(binding.relativeParentFav,"Não foi possivel processar sua requisiçaõ\nVerifique sua conexão e tente novamente.",Snackbar.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e("logx", "EDeleteAll: " + e.getMessage());
            Snackbar.make(binding.relativeParentFav,"Tivemos um problema ao processar sua solicitação. " +
                    "\nPor favor reinicie o app e tente novamente",Snackbar.LENGTH_LONG).show();
        }

    }

    public void loadUser(String uid) {
        UserService uService = new UserService();
        PreferencesApplication pApplication = new PreferencesApplication(this);
        binding.swipeRefreshFav.setRefreshing(true);
        userEntity = pApplication.getUserPrefs();
        if (userEntity != null && userEntity.getUid().equals(uid))
            getAllFav(userEntity.getIdUser());
        else {
            try{
                uService.getUserByUid(uid, new UserCallback() {
                    @Override
                    public void onResponse(Response<UserEntity> response) {
                        if (response.isSuccessful()) {
                            userEntity = response.body();
                            if (userEntity != null) {
                                pApplication.setUserPrefs(userEntity);
                                getAllFav(userEntity.getIdUser());
                            }
                        }else{
                            try{
                                JSONObject objectError = new JSONObject(response.errorBody().string());
                                Snackbar.make(binding.relativeParentFav,"Ops! "+objectError.getString("title"),Snackbar.LENGTH_LONG).show();

                            }catch(IOException | JSONException e){
                                Snackbar.make(binding.relativeParentFav,"Message "+e.getMessage(),Snackbar.LENGTH_LONG).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                });
            }catch (Exception e) {
                Log.e("logx", "EDeleteAll: " + e.getMessage());
                Toast.makeText(this, "Tivemos um problema ao recuperar os dados do usuário " +
                        "\nPor favor reinicie o app e tente novamente", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Metodo temporario que mostrara dados moados na listagem de favoritos
    public void showDataFav(List<ItemEntity> itens) {
        componentListener.fadeComponents(binding.recyclerItensFav, "alpha", 0f, 1f, 800, false);
        binding.recyclerItensFav.hasFixedSize();
        binding.recyclerItensFav.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter((Activity) this, itens, 1, binding.relativeResourcesFav);
        binding.recyclerItensFav.setAdapter(itemAdapter);
        itemAdapter.setOnClickListener(new ItemAdapter.OnClickListener() {
            @Override
            public void onItemClick(int position) {
                if (itens.size() != 0 && !DetailsActivity.status) {
                    Intent i = new Intent(FavouriteActivity.this, DetailsActivity.class);
                    i.putExtra("announcement", itens.get(position));
                    i.putExtra("images", (Serializable) itens.get(position).getImages());
                    i.putExtra("user", favourites.get(position).getUserEntity());
                    startActivity(i);
                }
            }

            @Override
            public void onDeleteClick(final int position) {
                final AlertDialog alerta = new AlertDialog.Builder(FavouriteActivity.this).create();
                final View v = getLayoutInflater().inflate(R.layout.layout_dialog, null);
                Button btnOk_dialog = v.findViewById(R.id.btnOk_dialog);
                Button btnNo_dialog = v.findViewById(R.id.btnNo_dialog);
                btnOk_dialog.setText(R.string.txtBtnYesDialog);
                btnNo_dialog.setText(R.string.txtBtnCancelDialog);
                TextView txtTitle_dialog = v.findViewById(R.id.txtTitle_dialog);
                TextView txtDialog_dialog = v.findViewById(R.id.txtDialog_dialog);
                txtTitle_dialog.setText(R.string.titleDialogFav);
                txtDialog_dialog.setText(R.string.textDialogFav);
                btnOk_dialog.setOnClickListener(view -> {
                    alerta.dismiss();
                    deleteSingle(favourites.get(position).getIdFavourite(), position);

                });
                btnNo_dialog.setOnClickListener(view1 -> {
                    alerta.dismiss();
                });
                alerta.setView(v);
                alerta.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alerta.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        status = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        status = true;
    }

    public void actFavParent(View view){
        NavUtils.navigateUpFromSameTask(FavouriteActivity.this);
    }

}


