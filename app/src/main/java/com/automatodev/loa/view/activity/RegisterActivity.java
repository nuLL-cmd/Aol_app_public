package com.automatodev.loa.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.AdressCallback;
import com.automatodev.loa.controller.callback.UserCallback;
import com.automatodev.loa.controller.service.CityService;
import com.automatodev.loa.controller.service.UserService;
import com.automatodev.loa.databinding.ActivityRegisterBinding;
import com.automatodev.loa.model.entity.CityEntity;
import com.automatodev.loa.model.entity.CountryEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.components.DialogUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private List<CityEntity> cityEntities;
    private View v;
    private ComponentListener cGlobal;
    private UserEntity userEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View viewParent = binding.getRoot();
        setContentView(viewParent);
        cGlobal = new ComponentListener(this);
        userEntity = new UserEntity();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            binding.edtEmailRegister.setText(account.getEmail());
            binding.edtNameRegister.setText(account.getGivenName());
            binding.edtLastNameRegister.setText(account.getFamilyName());
            userEntity.setUrlPhoto(account.getPhotoUrl().toString());
        }
        populeSpinnerState();
    }

    @Override
    public void onBackPressed() {
        Snackbar.make(binding.linearParentRegister, "Ops! Você precisa finalizar seu cadastro!\nÉ rapidinho! :D", Snackbar.LENGTH_LONG).show();
    }

    //Metodo para popular o spinner contendo a lista de estados na tela de registro
    public void populeSpinnerState() {
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerUFRegister.setAdapter(sAdapter);
        binding.spinnerUFRegister.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCityCloud(CountryEntity.populateUf().get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Metod que recupera todos os municipios pela api do ibge com base no id da UF passada como parametro
    private void getCityCloud(int id) {
        CityService cityService = new CityService();
        cityEntities = new ArrayList<>();
        binding.relativeCityRegister.setVisibility(View.VISIBLE);
        cityService.getCities(id, new AdressCallback() {
            @Override
            public void onResponse(Response<List<CityEntity>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        cityEntities = response.body();
                        binding.relativeCityRegister.setVisibility(View.GONE);
                        binding.spinnerCityRegister.setTitle("Selecione uma cidade");
                        List<String> cities = new ArrayList<>();
                        cityEntities.forEach(c -> cities.add(c.getNome()));
                        binding.spinnerCityRegister.setAdapter(new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, cities));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(binding.linearParentRegister, "Verifique sua conexão!", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    //Metodo genial de validação de campos vazios, caso um dos fields estejam vazios, chamam o metodo cGlobal da classe
    //ComponentsGlobal que realiza a troca do background para indicar o campo vazio
    public boolean validateFields() {
        int count = 0;
        EditText[] editTexts = new EditText[4];
        editTexts[0] = binding.edtNameRegister;
        editTexts[1] = binding.edtLastNameRegister;
        editTexts[2] = binding.edtEmailRegister;
        editTexts[3] = binding.edtPhoneRegister;
        for (EditText edit : editTexts) {
            if (edit.getText().toString().isEmpty()) {
                edit.setBackgroundResource(R.drawable.bg_edt_global_error);
                cGlobal.onTextListener(edit);
                count++;
            }

        }
        return count != 0;
    }

    //Finaliza a activity de registro dando espaço para a activity principal
    public void actRegisterMain(View view) {

        DialogUtils dialogUtils = new DialogUtils((Activity) this);
        AlertDialog dialogProgress = dialogUtils.dialogProgress("");
        View viewDialog = dialogUtils.getView();
        TextView txtMessage_dialog_progress = viewDialog.findViewById(R.id.txtMessage_dialog_progress);
        if (validateFields()) {
            Snackbar.make(binding.linearParentRegister,
                    "Ops! Ainda há campos a serem preenchidos", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (binding.edtPhoneRegister.getText().toString().trim().length() > 0 && binding.edtPhoneRegister.getText().toString().trim().length() < 14) {
            binding.edtPhoneRegister.setBackgroundResource(R.drawable.bg_edt_global_error);
            Snackbar.make(binding.linearParentRegister,
                    "Telefone deve seguir o padrão de 11 digitos", Snackbar.LENGTH_LONG).show();
        } else {
            dialogProgress.show();
            txtMessage_dialog_progress.setText("Criando registro");
            if (cityEntities.size() == 0)
                userEntity.setCity(null);
            else
                userEntity.setCity(cityEntities.get(binding.spinnerCityRegister.getSelectedItemPosition()).getNome());
            userEntity.setFirstName(binding.edtNameRegister.getText().toString());
            userEntity.setLastName(binding.edtLastNameRegister.getText().toString());
            userEntity.setEmail(binding.edtEmailRegister.getText().toString());
            userEntity.setPhone(binding.edtPhoneRegister.getText().toString());
            userEntity.setUf(binding.spinnerUFRegister.getSelectedItem().toString());
            userEntity.setCatDefault("Eletrônicos");
            userEntity.setUid(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            UserService userService = new UserService();
            userService.postUser(userEntity, new UserCallback() {
                @Override
                public void onResponse(Response<UserEntity> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            dialogUtils.dialogStatus(binding.linearParentRegister, "Salvando dados",
                                    "", "success.json", false);
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        sleep(1500);
                                        MainActivity.statusView = true;
                                        finish();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();

                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    String messageFailure = "Houve um erro ao processar sua requisição!\n" +
                            "Verifique sua conexão e tente novamente";
                    dialogUtils.dialogStatus(binding.linearParentRegister,
                            "Ops!", messageFailure, "error.json", true);
                }
            });
        }
    }
}