package com.automatodev.loa.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.AnnouncementCallback;
import com.automatodev.loa.controller.callback.OfferCallback;
import com.automatodev.loa.controller.callback.UserCallback;
import com.automatodev.loa.controller.service.ItemService;
import com.automatodev.loa.controller.service.OfferService;
import com.automatodev.loa.controller.service.UserService;
import com.automatodev.loa.databinding.ActivityOwnerBinding;
import com.automatodev.loa.model.entity.ImageEntity;
import com.automatodev.loa.model.entity.ItemEntity;
import com.automatodev.loa.model.entity.OfferEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.model.firebase.Authentication;
import com.automatodev.loa.view.adapter.AdapterOffer;
import com.automatodev.loa.view.adapter.ItemAdapter;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.components.DialogUtils;
import com.automatodev.loa.view.extras.PreferencesApplication;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.melnykov.fab.ScrollDirectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;

public class OwnerActivity extends AppCompatActivity {

    private ActivityOwnerBinding binding;
    private ItemAdapter itemAdapter;
    private FirebaseUser user;
    private AlertDialog dialog;
    private Authentication auth;
    private int countDelete;
    private String userEmail;
    List<ItemEntity> itemEntityList;
    private ComponentListener componentListener;
    public static boolean status;
    private PreferencesApplication pApplication;
    public static UserEntity userEntity;
    public static boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOwnerBinding.inflate(getLayoutInflater());
        View viewOner = binding.getRoot();
        setContentView(viewOner);
        itemEntityList = new ArrayList<>();
        pApplication = new PreferencesApplication(this);
        componentListener = new ComponentListener(this);
        auth = new Authentication();
        user = auth.getUser();
        binding.fabNewOwner.attachToRecyclerView(binding.recyclerItemsOwner, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                binding.fabNewOwner.show();
            }

            @Override
            public void onScrollUp() {
                binding.fabNewOwner.hide();
            }
        });
        binding.swipeRefreshMy.setOnRefreshListener(() -> {
            if (user != null)
                getAnnouncements(user.getUid());
        });
        binding.swipeRefreshMy.setColorSchemeResources(R.color.colorPrimaryDark2);
        getAnnouncements(user.getUid());
    }

    //No onStart é atribuido true variavel de verificação da instancia de telas
    @Override
    protected void onStart() {
        super.onStart();
        status = true;
    }

    //No onStop é atribuido false a variavel de verificação da instancia de telas
    @Override
    protected void onStop() {
        super.onStop();
        status = false;
    }

    public void getAnnouncements(String uid) {
        componentListener.fadeComponents(binding.recyclerItemsOwner, "alpha", 1f, 0f, 250, false);
        binding.swipeRefreshMy.setRefreshing(true);
        UserService uService = new UserService();
        try {
            uService.getUserByUid(uid, new UserCallback() {
                @Override
                public void onResponse(Response<UserEntity> response) {
                    binding.imgResourcesOwner.setImageResource(R.drawable.ic_undraw_surfer_m6jb);
                    binding.txtTitleResourcesOwner.setText(R.string.titleNoContent);
                    binding.txtMessageResourcesOwner.setText(R.string.messageNoContentOwner);
                    userEmail = response.body().getEmail();
                    itemEntityList.clear();
                    itemEntityList.addAll(response.body().getAnnounces());
                    showDataOwner(itemEntityList, response.body());
                    binding.swipeRefreshMy.setRefreshing(false);

                }

                @Override
                public void onFailure(Throwable t) {
                    if (itemAdapter != null) {
                        componentListener.fadeComponents(binding.recyclerItemsOwner, "alpha", 1f, 0f, 250, false);
                        itemEntityList.clear();
                        itemAdapter.notifyDataSetChanged();
                    }
                    binding.imgResourcesOwner.setImageResource(R.drawable.ic_undraw_notify_re_65on);
                    binding.txtTitleResourcesOwner.setText(R.string.titleErrorConnection);
                    binding.txtMessageResourcesOwner.setText(R.string.messageErrorConnectionOwner);
                    binding.relativeResourcesOwner.setVisibility(View.VISIBLE);

                }
            });
        } catch (Exception e) {
            Log.e("logx", "EGetAnnouncements: " + e.getMessage());
            Toast.makeText(this, "Tivemos um problema ao recuperar seus anúncios " +
                    "\nPor favor reinicie o app e tente novamente", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteAnnouncement(List<ItemEntity> itemEntityList, long positonData, int positionList) {
        DialogUtils dialogUtils = new DialogUtils(this);
        dialog = new AlertDialog.Builder(this).create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog, null);
        Button btnOk_dialog = view.findViewById(R.id.btnOk_dialog);
        Button btnNo_dialog = view.findViewById(R.id.btnNo_dialog);
        btnOk_dialog.setText(R.string.txtBtnYesDialog);
        btnNo_dialog.setText(R.string.txtBtnCancelDialog);
        TextView txtDialog_dialog = view.findViewById(R.id.txtDialog_dialog);
        TextView txtTitle_dialog = view.findViewById(R.id.txtTitle_dialog);
        txtTitle_dialog.setText(R.string.txtTitle_dialog);
        txtDialog_dialog.setText(R.string.txtDialog_dialog);
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        btnOk_dialog.setOnClickListener(v1 -> {
            countDelete = 0;
            dialog.dismiss();
            Toast.makeText(this, "position: " + positonData + " - " + positionList, Toast.LENGTH_SHORT).show();
            dialog = dialogUtils.dialogProgress("");
            View view1 = dialogUtils.getView();
            TextView txtMessage_dialog_progress = view1.findViewById(R.id.txtMessage_dialog_progress);
            txtMessage_dialog_progress.setText("Removendo arquivos");
            dialog.show();
            try {
                for (ImageEntity i : itemEntityList.get(positionList).getImages()) {
                    StorageReference storage = FirebaseStorage.getInstance().getReference("announcement")
                            .child(userEmail)
                            .child(itemEntityList.get(positionList).getUid())
                            .child(i.getName());
                    storage.delete().addOnSuccessListener(avoid -> {
                        if ((countDelete + 1) == itemEntityList.get(positionList).getImages().size())
                            deleteItemsServer(dialogUtils, itemEntityList, positonData, positionList);
                        countDelete++;
                    }).addOnFailureListener(t -> {
                        deleteItemsServer(dialogUtils, itemEntityList, positonData, positionList);
                    });
                }
            } catch (Exception e) {
                Log.e("logx", "EDeleteStorageItems: " + e.getMessage());
                dialogUtils.dialogStatus(binding.relativeParentMy, "Ops! Algo deu errado.",
                        "Parece que não pudemos deletar seu anúncio. Nosso administrador ja foi notificado!", "error.json", true);
            }

        });
        btnNo_dialog.setOnClickListener(v2 -> dialog.dismiss());
    }

    public void showDataOwner(List<ItemEntity> itemLIst, UserEntity userEntity) {
        binding.recyclerItemsOwner.hasFixedSize();
        binding.recyclerItemsOwner.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(this, itemLIst, 2,
                binding.relativeResourcesOwner);
        binding.recyclerItemsOwner.setAdapter(itemAdapter);
        componentListener.fadeComponents(binding.recyclerItemsOwner, "alpha", 0f, 1f, 800, false);
        itemAdapter.setOnLongItemClickListener(position -> {
            AlertDialog alerta = new AlertDialog.Builder(this).create();
            Objects.requireNonNull(alerta.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            View v = getLayoutInflater().inflate(R.layout.layout_dialog_options, null);
            Button btnEdit_options = v.findViewById(R.id.btnEdit_options);
            Button btnFinish_options = v.findViewById(R.id.btnFinish_options);
            Button btnOffer_options = v.findViewById(R.id.btnOffer_options);
            alerta.setView(v);
            alerta.show();
            btnEdit_options.setOnClickListener(view -> {
                Intent i = new Intent(this, NewActivity.class);
                i.putExtra("announcement", itemLIst.get(position));
                i.putExtra("user", userEntity);
                i.putExtra("images", (Serializable) itemLIst.get(position).getImages());
                startActivity(i);
                alerta.dismiss();
            });
            btnFinish_options.setOnClickListener(view -> {
                alerta.dismiss();
                deleteAnnouncement(itemLIst, itemLIst.get(position).getIdAnnouncement(), position);

            });
            btnOffer_options.setOnClickListener(view -> {
                OfferService oService = new OfferService();
                final AlertDialog dialogOffers = new AlertDialog.Builder(this).create();
                dialogOffers.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                View viewOffers = getLayoutInflater().inflate(R.layout.layout_dialog_offer, null);
                final RecyclerView recycler_dialogOffer = viewOffers.findViewById(R.id.recycler_dialogOffer);
                final SwipeRefreshLayout swipeRefresh_dialogOffer = viewOffers.findViewById(R.id.swipeRefresh_dialogOffer);
                final RelativeLayout relativeResources_dialogOffer = viewOffers.findViewById(R.id.relativeResources_dialogOffer);
                final TextView txtTitle_dialogOffer = viewOffers.findViewById(R.id.txtTitle_dialogOffer);
                txtTitle_dialogOffer.setText("Ofertas disponiveis");
                recycler_dialogOffer.setHasFixedSize(true);
                recycler_dialogOffer.setLayoutManager(new LinearLayoutManager(this));
                swipeRefresh_dialogOffer.setRefreshing(true);
                swipeRefresh_dialogOffer.setOnRefreshListener(() -> fethOffers(itemLIst.get(position).getIdAnnouncement(),oService, recycler_dialogOffer, swipeRefresh_dialogOffer, relativeResources_dialogOffer));
                fethOffers(itemLIst.get(position).getIdAnnouncement(),oService, recycler_dialogOffer, swipeRefresh_dialogOffer, relativeResources_dialogOffer);

                dialogOffers.setView(viewOffers);
                dialogOffers.show();
            });

        });
    }

    private void fethOffers(long idItem, OfferService oService, RecyclerView recycler_dialogOffer, SwipeRefreshLayout swipeRefresh_dialogOffer, RelativeLayout relativeResources_dialogOffer) {
        oService.getAllByItem(idItem,"Confirmada", new OfferCallback() {
            @Override
            public void onResponse(Response<List<OfferEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdapterOffer adapterOffer = new AdapterOffer(response.body(), "owner", OwnerActivity.this, relativeResources_dialogOffer);
                    recycler_dialogOffer.setAdapter(adapterOffer);
                    swipeRefresh_dialogOffer.setRefreshing(false);
                    componentListener.fadeComponents(recycler_dialogOffer, "alpha", 0f, 1f, 800, false);
                    adapterOffer.setOnItemClickListener(new AdapterOffer.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            callNumber(response.body().get(position).getItem().getPhone());
                        }

                        @Override
                        public void onItemDeleteClick(int position) {
                            adapterOffer.notifyItemRemoved(position);
                            OfferEntity offerEntity = response.body().get(position);
                            offerEntity.setStatus("Rejeitada");

                            oService.updateOffer(offerEntity.getIdOffer(), offerEntity, new OfferCallback() {
                                @Override
                                public void onResponse(Response<List<OfferEntity>> response) {
                                }

                                @Override
                                public void onResponseId(Response<Long> response) {
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                }
                            });
                            response.body().remove(position);

                        }
                    });
                }else{
                    swipeRefresh_dialogOffer.setRefreshing(false);
                    try{
                        JSONObject error = new JSONObject(response.errorBody().string());
                        Toast.makeText(OwnerActivity.this, "Tivemos um problema ao processar sua requisição!\nErro: "+error.getString("message"), Toast.LENGTH_LONG).show();
                    }catch (JSONException | IOException e){
                        e.printStackTrace();
                        Toast.makeText(OwnerActivity.this, "Tivemos um problema ao processar sua requisição! \nError: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onResponseId(Response<Long> response) {
            }

            @Override
            public void onFailure(Throwable t) {
                swipeRefresh_dialogOffer.setRefreshing(false);
                t.printStackTrace();
                Toast.makeText(OwnerActivity.this, "Tivemos um problema ao processar sua requisição! \nVerifique sua conexão e tente novamentw", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void callNumber(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    public void deleteItemsServer(DialogUtils dialogUtils, List<ItemEntity> itemEntityList, Long positionData, int positionList) {
        ItemService aService = new ItemService();
        try {
            aService.deleteAnnouncement(positionData, new AnnouncementCallback() {
                @Override
                public void onResponse(Response<ItemEntity> response) {
                    itemAdapter.notifyItemRemoved(positionList);
                    itemEntityList.remove(positionList);
                    dialogUtils.dialogStatus(binding.relativeParentMy, "Sucesso!",
                            "Anúncio Removido!", "success.json", true);
                }

                @Override
                public void onFailure(Throwable t) {
                    dialogUtils.dialogStatus(binding.relativeParentMy, "Ops! Algo deu errado.",
                            "Parece que não pudemos deletar seu anúncio. Tente novamente", "error.json", true);
                }

                @Override
                public void onResponseAll(Response<List<ItemEntity>> response) {
                }
            });
        } catch (Exception e) {
            Log.e("logx", "EDeleteItemServer: " + e.getMessage());
            dialogUtils.dialogStatus(binding.relativeParentMy, "Ops! Algo deu errado.",
                    "Parece que não pudemos deletar seu anúncio. Um log ja foi enviado e ja fomos notificados!", "error.json", true);
        }

    }

    public void actOwnerParent(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    public boolean verifyConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnectedOrConnecting();
    }

    public void actOwnerNew(View view) {
        userEntity = pApplication.getUserPrefs();
        if (verifyConnection() && userEntity != null && !NewActivity.status) {
            Intent i = new Intent(this, NewActivity.class);
            i.putExtra("user", userEntity);
            startActivity(i);
        } else
            Snackbar.make(binding.relativeParentMy,
                    "Ops! Ha algo errado na sua solcitação\nVerifique sua conexão e reinicie o aplicativo", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (update) {
            getAnnouncements(user.getUid());
            update = false;
        }
    }

}