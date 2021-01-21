package com.automatodev.loa.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.FavCallback;
import com.automatodev.loa.controller.callback.OfferCallback;
import com.automatodev.loa.controller.service.FavService;
import com.automatodev.loa.controller.service.OfferService;
import com.automatodev.loa.databinding.ActivityDetailsBinding;
import com.automatodev.loa.model.entity.FavouriteEntity;
import com.automatodev.loa.model.entity.ImageEntity;
import com.automatodev.loa.model.entity.ItemEntity;
import com.automatodev.loa.model.entity.OfferEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.extras.FormatTools;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static boolean status;
    private ActivityDetailsBinding binding;
    private Long idFavourite;
    private ItemEntity i;
    private UserEntity u;
    private FavouriteEntity f;
    private List<ImageEntity> images;
    private EditText edtSubject_layoutSend;
    private EditText edtMessage_layoutSend;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Area para chamada de metodos ao iniciar a activity
        binding.mapViewDetails.onCreate(savedInstanceState);
        binding.mapViewDetails.getMapAsync(this);
        binding.fadingSecurityDetails.setTexts(R.array.security);
        binding.edtValueDetails.setLocale(new Locale("pt", "br"));
        loadData();

        binding.imgProductDetails.setImageListener((position, imageView) -> {
            if (i.getImages().size() != 0 && i.getImages() != null) {
                Glide.with(DetailsActivity.this).load(i.getImages().get(position).getUrlImage())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
            }
            imageView.setOnClickListener(v -> {
                if (images != null) {
                    Intent intent = new Intent(DetailsActivity.this, PhotoActivity.class);
                    intent.putExtra("images", (Serializable) images);
                    startActivity(intent);
                } else
                    Snackbar.make(binding.relativeContentDetails, "Não foi possivel abrir o visualizador de imagens.", Snackbar.LENGTH_LONG).show();
                ;
            });
        });

    }

    //No onStop é atribuido false a variavel de verificação da instancia de telas
    @Override
    protected void onStop() {
        super.onStop();
        status = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapViewDetails.onResume();
    }

    //No onStart é atribuido true variavel de verificação da instancia de telas
    @Override
    protected void onStart() {
        super.onStart();
        status = true;
    }

    //Metodo que retorna a activity main atraves do botao voltar
    public void actDetailsMain(View view) {
        NavUtils.navigateUpFromSameTask(DetailsActivity.this);
    }

    public void loadData() {
        FormatTools formatTools = new FormatTools();
        ComponentListener cGlobal = new ComponentListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            i = getIntent().getParcelableExtra("announcement");
            u = getIntent().getParcelableExtra("user");
            images = (List<ImageEntity>) bundle.getSerializable("images");
            try{
                if (i != null) {
                    if (u != null)
                        verifyFavourite(u.getIdUser(), i.getIdAnnouncement());

                    i.setImages(images);
                    binding.imgProductDetails.setPageCount(i.getImages().size());
                    binding.txtTitleDetails.setText(i.getTitle());
                    binding.txtDescriptionDetails.setText(i.getDescription());
                    binding.txtPriceDetails.setText(formatTools.decimalFormat(i.getPrice()));
                    binding.txtUFDetails.setText(i.getUf());
                    binding.txtCityDetails.setText(i.getCity());
                    binding.txtSituationDetails.setText("Produto " + i.getSituation());
                    binding.txtDateDatails.setText(formatTools.dateFormatWithHour(i.getDateCad()));
                    if (i.getUserEntity().getUrlPhoto() != null) {
                        binding.txtNameOwnerDetails.setText(i.getUserEntity().getFirstName() + " " + i.getUserEntity().getLastName());
                        binding.txtSinceDetails.setText(formatTools.dateFormatnoHour(i.getUserEntity().getDateSince()));
                        Glide.with(DetailsActivity.this)
                                .load(i.getUserEntity().getUrlPhoto())
                                .addListener(cGlobal.glideListener(binding.relativeProgressPhotoDetails))
                                .into(binding.imgOwnerNew);
                    }
                }

            }catch(Exception e){
                Log.d("logx","Log loadData: "+e.getMessage());
                Snackbar.make(binding.relativeContentDetails,"Estamos tendo problemas para acessar os dados deste anuncio, por favor tente novamente mais tarde.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void sendWhatsapp(String phone) {
        String uri = "https://api.whatsapp.com/send/?phone=55" + phone;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    public void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                "tel", phone, null));
        startActivity(intent);
    }

    public void diallerUser(View view) {
        callPhone(i.getPhone());
    }

    public void messageUser(View view) {
        sendWhatsapp(i.getPhone());
    }

    public void verifyFavourite(Long idUser, Long idItem) {
        binding.relativeProgressFavDetails.setVisibility(View.VISIBLE);
        FavService fService = new FavService();
        if (idUser != null && idUser != 0) {
            fService.getByidUser(idUser, new FavCallback() {
                @Override
                public void onResponse(Response<List<FavouriteEntity>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        binding.relativeProgressFavDetails.setVisibility(View.GONE);
                        List<FavouriteEntity> fav;
                        fav = response.body();
                        if (fav.size() == 0) {
                            binding.imgFavouriteDetails.setVisibility(View.VISIBLE);
                            binding.imgFavouriteDetails.setTag("nonFav");
                            return;
                        }
                        for (FavouriteEntity f : fav) {
                            if (f.getAnnouncementEntity().getIdAnnouncement().equals(idItem)) {
                                idFavourite = f.getIdFavourite();
                                binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_on);
                                binding.imgFavouriteDetails.setTag("fav");
                                binding.imgFavouriteDetails.setVisibility(View.VISIBLE);
                                break;

                            } else {
                                binding.imgFavouriteDetails.setVisibility(View.VISIBLE);
                                binding.imgFavouriteDetails.setTag("nonFav");
                            }
                        }
                    } else {
                        try {
                            JSONObject objectError = new JSONObject(response.errorBody().string());
                            Snackbar.make(binding.relativeContentDetails, "Ops! " + objectError.getString("title"), Snackbar.LENGTH_LONG).show();
                        } catch (IOException | JSONException e) {
                            Snackbar.make(binding.relativeContentDetails, "Message: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                }

                @Override
                public void onResponseId(Response<Long> response) {
                }
            });
        }
    }

    public void checkUncheckFav(View view) {
        if (binding.imgFavouriteDetails.getTag().equals("fav") && idFavourite != null) {
            deleteFavourite(idFavourite);
            binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_off);
            binding.imgFavouriteDetails.setTag("nonFav");

        } else {
            saveFavourite(u, i);
            binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_on);
            binding.imgFavouriteDetails.setTag("fav");
        }
    }

    public void saveFavourite(UserEntity u, ItemEntity i) {
        FavService fService = new FavService();
        f = new FavouriteEntity(new UserEntity(u.getIdUser()),new ItemEntity(i.getIdAnnouncement()));
        fService.postFavourites(f, new FavCallback() {
            @Override
            public void onResponse(Response<List<FavouriteEntity>> response) {
            }

            @Override
            public void onFailure(Throwable t) {
                if (binding.imgFavouriteDetails.getTag().equals("fav")) {
                    binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_off);
                    binding.imgFavouriteDetails.setTag("nonFav");
                } else {
                    binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_on);
                    binding.imgFavouriteDetails.setTag("fav");
                }
                Snackbar.make(binding.relativeContentDetails, "Não foi possivel atender sua requisição!\nVerifique sua conexão!",
                        Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponseId(Response<Long> response) {
                if (response.isSuccessful() && response.code() == 200)
                    idFavourite = response.body();
                else {
                    try {
                        JSONObject objectError = new JSONObject(response.errorBody().string());
                        Snackbar.make(binding.relativeContentDetails, "Ops! " + objectError.getString("title"), Snackbar.LENGTH_LONG).show();
                        binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_off);
                        binding.imgFavouriteDetails.setTag("nonFav");
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(binding.relativeContentDetails, "Ops! Parece que este anúncio não está mais disponivel", Snackbar.LENGTH_LONG).show();
                        binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_off);
                        binding.imgFavouriteDetails.setTag("nonFav");
                    }

                }
            }
        });
    }

    public void deleteFavourite(long idFavourite) {
        FavService fService = new FavService();
        fService.deleteById(idFavourite, new FavCallback() {
            @Override
            public void onResponse(Response<List<FavouriteEntity>> response) {
            }

            @Override
            public void onFailure(Throwable t) {
                if (binding.imgFavouriteDetails.getTag().equals("fav")) {
                    binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_off);
                    binding.imgFavouriteDetails.setTag("nonFav");
                } else {
                    binding.imgFavouriteDetails.setImageResource(R.drawable.ic_favorite_on);
                    binding.imgFavouriteDetails.setTag("fav");
                }
                Snackbar.make(binding.relativeContentDetails, "Não foi possivel atender sua requisição!\nVerifique sua conexão!",
                        Snackbar.LENGTH_LONG).show();

            }

            @Override
            public void onResponseId(Response<Long> response) {
                if (!response.isSuccessful()) {
                    try {
                        JSONObject objectError = new JSONObject(response.errorBody().string());
                        Snackbar.make(binding.relativeContentDetails, "Ops! " + objectError.getString("title"), Snackbar.LENGTH_LONG).show();
                    } catch (IOException | JSONException e) {
                        Snackbar.make(binding.relativeContentDetails, "Ops! Parece que este anúncio não está mais disponivel", Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            if ( i.getLang() != 0 && i.getLat() != 0){
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.addMarker(new MarkerOptions().position(new LatLng(i.getLat(), i.getLang()))
                        .title("O produto esta aqui!"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(i.getLat(), i.getLang()), 15));

            }else{
                binding.txtLocationNotDetails.setVisibility(View.VISIBLE);
                binding.imgLocationNotDetails.setVisibility(View.VISIBLE);
            }

        }catch (Exception e){
            Log.e("logx","ErrorLoadMap: "+e.getMessage());
            finish();

        }

    }

    public void saveOffer(View view){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null && u == null){
            Snackbar.make(binding.relativeContentDetails,"Você precisa esta logado para dar um lance!", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (binding.edtValueDetails.getRawValue() == 0){
            Snackbar.make(binding.relativeContentDetails,"Ops! \nAcho que o vendedor não pode aceitar este valor!", Snackbar.LENGTH_LONG).show();
            return;

        }
            OfferEntity offer = new OfferEntity(binding.edtValueDetails.getRawValue() / 100.00,"Confirmada",
                    new UserEntity(u.getIdUser()),new ItemEntity(i.getIdAnnouncement()));
            OfferService oService = new OfferService();
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Só um momento");
        progress.setMessage("Validando e enviando oferta...");
        progress.show();
            try{
                oService.postOffer(offer, new OfferCallback() {
                    @Override
                    public void onResponse(Response<List<OfferEntity>> response) {
                    }

                    @Override
                    public void onResponseId(Response<Long> response) {
                        progress.dismiss();
                        if (response.isSuccessful()){
                            Snackbar.make(binding.relativeContentDetails,"Sucesso! \nSeu lance foi enviado para o anunciante.", Snackbar.LENGTH_LONG).show();
                            binding.edtValueDetails.setValue(0);
                        } else{
                            try{
                                assert response.errorBody() != null;
                                JSONObject objectError = new JSONObject(response.errorBody().string());
                                Snackbar.make(binding.relativeContentDetails,"Ops! "+objectError.getString("title"),Snackbar.LENGTH_LONG).show();
                            }catch(IOException | JSONException e){
                                e.printStackTrace();
                                Snackbar.make(binding.relativeContentDetails,"Ops! Parece que este anúncio não está mais disponivel",Snackbar.LENGTH_LONG).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        progress.dismiss();
                        Snackbar.make(binding.relativeContentDetails,
                                "Tivemos um problema ao processar sua requisição.\nVerifique sua conexão e tente novamente.", Snackbar.LENGTH_LONG).show();
                    }
                });
            }catch(Exception e){
                Snackbar.make(binding.relativeContentDetails,
                        "Tivemos um problema ao processar sua requisição.\nPor favor reinicie o app e tente novamente.", Snackbar.LENGTH_LONG).show();
            }

    }


    public void showBottomSendEmail(View view) {
       BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_send_email, null);
         edtSubject_layoutSend = viewDialog.findViewById(R.id.edtSubject_layoutSend);
         edtMessage_layoutSend = viewDialog.findViewById(R.id.edtMessage_layoutSend);
         edtSubject_layoutSend.setText(i.getTitle());
         edtSubject_layoutSend.setEnabled(false);
        Button btnSend_layoutSend = viewDialog.findViewById(R.id.btnSend_layoutSend);
        edtSubject_layoutSend.requestFocus();
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.show();
        btnSend_layoutSend.setOnClickListener(v -> sendEmail());

    }

    private void sendEmail() {
        String subject = edtSubject_layoutSend.getText().toString();
        String message = edtMessage_layoutSend.getText().toString();
        String[] toList = {"aol.automatodev@gmail.com"};
        if (subject.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "O email requer um titule e uma mensagem!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, toList);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("message/rfc822");
        try {
            startActivity(Intent.createChooser(intent, "Escolha seu cliente de e-mail"));
            Toast.makeText(this, "Obriagdo pelo seu parecer!", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "Não ha nenhum cliente de e-mail instalado.", Toast.LENGTH_SHORT).show();
        }

    }

}
