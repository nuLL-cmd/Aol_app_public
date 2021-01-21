package com.automatodev.loa.view.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.AdressCallback;
import com.automatodev.loa.controller.callback.UploadCallback;
import com.automatodev.loa.controller.callback.UserCallback;
import com.automatodev.loa.controller.service.CityService;
import com.automatodev.loa.controller.service.UserService;
import com.automatodev.loa.databinding.ActivityProfileBinding;
import com.automatodev.loa.model.entity.CategoryEntity;
import com.automatodev.loa.model.entity.CityEntity;
import com.automatodev.loa.model.entity.CountryEntity;
import com.automatodev.loa.model.entity.ItemEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.model.firebase.Authentication;
import com.automatodev.loa.model.firebase.StorageTools;
import com.automatodev.loa.view.adapter.ItemAdapter;
import com.automatodev.loa.view.adapter.SpinnerCustomAdapter;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.components.DialogUtils;
import com.automatodev.loa.view.extras.FormatTools;
import com.automatodev.loa.view.extras.PreferencesApplication;
import com.automatodev.loa.view.extras.RequestPermission;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    public static boolean status;
    private TextView txtMessage_dialog_progress;
    private ActivityProfileBinding binding;
    private SpinnerCustomAdapter spinnerAdapter;
    private CityService cityService;
    private List<CityEntity> lc;
    private List<ItemEntity> itens;
    private RequestPermission requestPermissions;
    private FormatTools toolsFormat;
    private UserEntity userProfile;
    private UserService uService;
    private Uri uri;
    private StorageTools storageTools;
    private AlertDialog dialogOperation;
    private ComponentListener cGlobal;
    private DialogUtils dialogUtils;
    private PreferencesApplication pApplication;
    private Authentication auth;
    private ItemAdapter adapter;
    private ObjectAnimator animator;

    @SuppressLint("InflateParams")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //Area para instanciação de elementos e objetos
        cityService = new CityService();
        requestPermissions = new RequestPermission(this, null);
        toolsFormat = new FormatTools();
        uService = new UserService();
        storageTools = new StorageTools();
        dialogUtils = new DialogUtils(this);
        cGlobal = new ComponentListener(this);
        pApplication = new PreferencesApplication(this);
        animator = ObjectAnimator.ofFloat(binding.recyclerItensProfile, "alpha",
                0f, 1f);
        auth = new Authentication();
        //------------------
        //Area para chamada de metodos ao iniciar a activity
        loadUser();
        //------------------
    }

    //Metodo que popula o spinner contendo os estados
    private void populeSpinnerState(int position) {
        ArrayAdapter<CharSequence> ufAdapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        ufAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerUF.setAdapter(ufAdapter);
        binding.spinnerUF.setSelection(position);
        binding.spinnerUF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCityCloud(CountryEntity.populateUf().get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void logoffUser(View view) {
        if (auth.getUser() != null) {
            final AlertDialog alerta = new AlertDialog.Builder(this).create();
            View v = getLayoutInflater().inflate(R.layout.layout_dialog, null);
            Button btnOk_dialog = v.findViewById(R.id.btnOk_dialog);
            Button btnNo_dialog = v.findViewById(R.id.btnNo_dialog);
            btnOk_dialog.setText(R.string.txtBtnYesDialog);
            btnNo_dialog.setText(R.string.txtBtnCancelDialog);
            TextView txtTitle_dialog = v.findViewById(R.id.txtTitle_dialog);
            TextView txtDialog_dialog = v.findViewById(R.id.txtDialog_dialog);
            txtTitle_dialog.setText(R.string.titleDialogLogoff);
            txtDialog_dialog.setText(R.string.textDialogLogoff);
            btnOk_dialog.setOnClickListener(viewOnClick -> {
                auth.userSingOut();
                alerta.dismiss();
                MainActivity.statusView = true;
                finish();
            });
            btnNo_dialog.setOnClickListener(viewBtnNo -> {
                alerta.dismiss();
            });
            alerta.setView(v);
            alerta.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alerta.show();

        }
    }

    //Metodo que popula o spinner contendo as categorias
    private void populeSpinnerCategories(int position) {
        spinnerAdapter = new SpinnerCustomAdapter(this, CategoryEntity.getCategory());
        binding.spinCategoryProfile.setAdapter(spinnerAdapter);
        binding.spinCategoryProfile.setSelection(position);
    }

    //Metodo que popula o spinner contendo os estados
    private void populeSpinnerCity(List<CityEntity> lc) {
        binding.spinnerCityProfile.setTitle("Selecione uma cidade");
        List<String> cities = new ArrayList<>();
        lc.forEach(l -> cities.add(l.getNome()));
        binding.spinnerCityProfile.setAdapter(new ArrayAdapter<>(this,  android.R.layout.simple_spinner_dropdown_item, cities));
        for (int i = 0; i < lc.size(); i++) {
            if (lc.get(i).getNome().equals(userProfile.getCity()))
                binding.spinnerCityProfile.setSelection(i);
        }

    }

    //Metodo sobescrito que verifica o requestCode se é da camera ou da biblioteca, e faz o setup da imagem no ImageView atraves do Glide.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        uri = data.getData();
                        Glide.with(this).load(uri)
                                .into(binding.imgUserProfile);
                    }
                }
                break;
        }
    }

    //Metodo para escolher a foto apartir da GALERIA
    public void pickLib(View view) {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (requestPermissions.verifyPermissonSingle(permission)) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        }
    }

    //Metodo da classe que monitora as permissoes negadas e dispara uma msg caso o usuario insista em nao fornecer as permissoes
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                Snackbar.make(binding.relativeParentProfile, "Você precisa dessa permissão!", Snackbar.LENGTH_LONG).show();
            else
                pickLib(binding.relativeParentProfile);
        }
    }

    //Metodo que recupera os dados do usuario da activity principal, valida e distribui para os demais campos da activity de perfil
    public void loadUser() {
        userProfile = pApplication.getUserPrefs();
        if (userProfile != null) {
            getDataUser(userProfile.getIdUser());
            for (int i = 0; i < CountryEntity.populateUf().size(); i++) {
                if (CountryEntity.populateUf().get(i).getSigla().equals(userProfile.getUf()))
                    populeSpinnerState(i);

            }
            for (int i = 0; i < CategoryEntity.getCategory().size(); i++) {
                if (CategoryEntity.getCategory().get(i).getOption().equals(userProfile.getCatDefault()))
                    populeSpinnerCategories(i);
            }
            binding.txtNameUserProfile.setText(userProfile.getFirstName() + " " + userProfile.getLastName());
            binding.edtNameProfile.setText(userProfile.getFirstName());
            binding.edtLastNameProfile.setText(userProfile.getLastName());
            binding.edtEmailProfile.setText(userProfile.getEmail());
            binding.edtPhoneProfile.setText(userProfile.getPhone());
            binding.txtAmountMain.setText(toolsFormat.decimalFormat(userProfile.getTotal()));
            binding.txtSinceDetails.setText(toolsFormat.dateFormatnoHour(userProfile.getDateSince()));
            if (userProfile.getUrlPhoto() != null) {
                Glide.with(this).load(userProfile.getUrlPhoto())
                        .addListener(cGlobal.glideListener(binding.relativeProgressPhoto))
                        .into(binding.imgUserProfile);
            }
        } else {
            Snackbar.make(binding.relativeParentProfile, "Houve um erro ao processar sua solicitação!" +
                    "\nFeche o app e tente novamente", Snackbar.LENGTH_LONG).show();
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(2000);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }

    //Metodo temporario a ser implementado para salvar as alterações do perfil do ususario
    public void saveUpdates(View view) {
        if (binding.edtPhoneProfile.getText().toString().isEmpty()){
            Snackbar.make(binding.relativeParentProfile,"Campo telefone não pode ser vazio!",Snackbar.LENGTH_LONG).show();
            return;
        }
        if (binding.edtPhoneProfile.getText().toString().trim().length() > 0 && binding.edtPhoneProfile.getText().toString().trim().length() < 14){
            Snackbar.make(binding.relativeParentProfile,"Telefone deve seguir o padrão de 11 digitos", Snackbar.LENGTH_LONG).show();
            return;
        }
        userProfile.setUf(binding.spinnerUF.getSelectedItem().toString());
        if (lc.size() == 0) {
            Snackbar.make(binding.relativeParentProfile
                    , "Ops! Aguarde o carregamento da lista de cidades por UF :D ", Snackbar.LENGTH_LONG).show();
            return;
        }
        userProfile.setCity(lc.get(binding.spinnerCityProfile.getSelectedItemPosition()).getNome());
        userProfile.setCatDefault(CategoryEntity.getCategory().get(binding.spinCategoryProfile.getSelectedItemPosition()).getOption());
        userProfile.setPhone(binding.edtPhoneProfile.getText().toString());
        dialogOperation = dialogUtils.dialogProgress("");
        View v = dialogUtils.getView();
        txtMessage_dialog_progress = v.findViewById(R.id.txtMessage_dialog_progress);
        userProfile.setAnnounces(null);
        dialogOperation.show();
        if (uri != null) {
            txtMessage_dialog_progress.setText("Enviando imagem");
            storageTools.uploadPhotoUser("photoUser", userProfile.getUid(), uri, new UploadCallback() {
                @Override
                public void onSuccess(Uri uri) {
                    userProfile.setUrlPhoto(uri.toString());
                    updateUser(userProfile);
                }

                @Override
                public void onFailure(Exception e) {
                    String snackMessage = "Houve um erro ao processar sua solicitação!\nVerifique sua conexão e tente novamente";
                    dialogUtils.dialogStatus(binding.relativeParentProfile
                            , "Ops!Temos um problema.", snackMessage, "error.json", true);

                }

                @Override
                public void onSuccess() {
                }
            });
        } else {
            updateUser(userProfile);

        }
    }

    //Metodo a ser chamado pra atualziar o usuario do banco, passando como parametro um usuario e um id unico
    public void updateUser(UserEntity user) {

        txtMessage_dialog_progress.setText("Atualizando dados");
        uService.updateUser(user.getIdUser(), user, new UserCallback() {
            @Override
            public void onResponse(Response<UserEntity> response) {
                pApplication.setUserPrefs(user);
                saveInitPrefs(user.getUf(), user.getCity());
                MainActivity.statusView = true;
                dialogUtils.dialogStatus(binding.relativeParentProfile, "Tudo certo!",
                        "Dados atualizados com sucesso!", "success.json", true);
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    String snackMessage = "Não foi possivel conectar ao servidor\nVerifique sua conexão com a internet";
                    dialogUtils.dialogStatus(binding.relativeParentProfile, "Ops! Algo esta errado.",
                            snackMessage, "error.json", true);
                } else if (t instanceof IOException) {
                    String snackMessage = "Houve um erro ao processar sua solicitação!\nPor favor tente novamente mais tarde.";
                    dialogUtils.dialogStatus(binding.relativeParentProfile, "Ops! Algo esta errado.",
                            snackMessage, "error.json", true);
                }
            }
        });
    }

    public void saveInitPrefs(String uf, String city){
        SharedPreferences prefs = getSharedPreferences("initData",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("uf",uf);
        editor.putString("city",city);
        editor.apply();
    }

    //Metodo que traz todos os municios com base no codigo da UF passada no parametro
    private void getCityCloud(int id) {
        lc = new ArrayList<>();
        binding.relCityProgressProfile.setVisibility(View.VISIBLE);
        cityService.getCities(id, new AdressCallback() {
            @Override
            public void onResponse(Response<List<CityEntity>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        lc = response.body();
                        binding.relCityProgressProfile.setVisibility(View.GONE);
                        populeSpinnerCity(lc);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(binding.relativeParentProfile,
                        "Verifique sua conexão!", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    //Metodo que retorna a activity main sem redefinir seu estado atual
    public void actProfileMain(View view) {
        NavUtils.navigateUpFromSameTask(ProfileActivity.this);
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


    public void getDataUser(Long id) {
        binding.swipeRefreshProfile.setColorSchemeResources(R.color.colorPrimaryDark2);
        binding.swipeRefreshProfile.setRefreshing(true);
        itens = new ArrayList<>();
        uService.getUserById(id, new UserCallback() {
            @Override
            public void onResponse(Response<UserEntity> response) {
                itens = response.body().getAnnounces();
                binding.txtMyItems.setText(String.valueOf(itens.size()));
                binding.swipeRefreshProfile.setRefreshing(false);

                showData(itens);
            }
            @Override
            public void onFailure(Throwable t) {
                    binding.relativeResourcesProfile.setVisibility(View.VISIBLE);
                    binding.imgResourcesProfile.setImageResource(R.drawable.ic_undraw_notify_re_65on);
                    binding.txtMessageResourcesProfile.setText(R.string.messageErrorConnectionFav);
                    Snackbar.make(binding.relativeParentProfile, "Houve um problema ao processar sua requisição! " +
                            "\nRecarregue o app e tente novamente", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void showData(List<ItemEntity> itens) {
        animator.start();
        binding.recyclerItensProfile.hasFixedSize();
        binding.recyclerItensProfile.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.HORIZONTAL, false));
        adapter = new ItemAdapter(this, itens, binding.relativeResourcesProfile, true, 2);
        binding.recyclerItensProfile.setAdapter(adapter);
    }

}
