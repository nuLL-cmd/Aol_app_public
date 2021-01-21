package com.automatodev.loa.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.AdressCallback;
import com.automatodev.loa.controller.callback.AuthCallback;
import com.automatodev.loa.controller.callback.UserCallback;
import com.automatodev.loa.controller.service.CityService;
import com.automatodev.loa.controller.service.UserService;
import com.automatodev.loa.model.entity.CityEntity;
import com.automatodev.loa.model.entity.CountryEntity;
import com.automatodev.loa.model.entity.UserEntity;
import com.automatodev.loa.model.firebase.Authentication;
import com.automatodev.loa.view.activity.MainActivity;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.components.DialogUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Response;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private List<CityEntity> cityEntities;
    private View v;
    private Context context;
    private ComponentListener cGlobal;

    private EditText edtName_register;
    private EditText edtLastName_register;
    private EditText edtEmail_register;
    private EditText edtPhone_register;
    private SearchableSpinner spinnerCity_register;
    private Spinner spinnerUF_register;
    private EditText edtPassword_register;
    private RelativeLayout relativeCity_register;
    private ImageButton btnGo_register;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Area para a instanciação de componentes
        v = inflater.inflate(R.layout.fragment_register, container, false);
        cGlobal = new ComponentListener(context);
        edtName_register = v.findViewById(R.id.edtName_register);
        edtLastName_register = v.findViewById(R.id.edtLastName_register);
        edtEmail_register = v.findViewById(R.id.edtEmail_register);
        edtPhone_register = v.findViewById(R.id.edtPhone_register);
        spinnerCity_register = v.findViewById(R.id.spinnerCity_register);
        spinnerUF_register = v.findViewById(R.id.spinnerUF_register);
        edtPassword_register = v.findViewById(R.id.edtPassword_register);
        btnGo_register = v.findViewById(R.id.btnGo_register);
        relativeCity_register = v.findViewById(R.id.relativeCity_register);
        //Area para chamada de metodos ao iniciar a activity
        populeSpinnerState();
        btnGo_register.setOnClickListener(this);
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGo_register:
                actRegisterMain();
                break;
        }
    }

    //Metodo para popular o spinner contendo a lista de estados na tela de registro
    public void populeSpinnerState() {
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(context, R.array.states, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUF_register.setAdapter(sAdapter);
        spinnerUF_register.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        relativeCity_register.setVisibility(View.VISIBLE);
        cityService.getCities(id, new AdressCallback() {
            @Override
            public void onResponse(Response<List<CityEntity>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        cityEntities = response.body();
                        relativeCity_register.setVisibility(View.GONE);
                        spinnerCity_register.setTitle("Selecione uma cidade");
                        List<String> cities = new ArrayList<>();
                        cityEntities.forEach(c -> cities.add(c.getNome()));
                        spinnerCity_register.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, cities));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(v, "Verifique sua conexão!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    //Metodo genial de validação de campos vazios, caso um dos fields estejam vazios, chamam o metodo cGlobal da classe
    //ComponentsGlobal que realiza a troca do background para indicar o campo vazio
    public boolean validateFields() {
        int count = 0;
        EditText[] editTexts = new EditText[5];
        editTexts[0] = edtName_register;
        editTexts[1] = edtLastName_register;
        editTexts[2] = edtEmail_register;
        editTexts[3] = edtPhone_register;
        editTexts[4] = edtPassword_register;
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
    public void actRegisterMain() {
        UserEntity userEntity = new UserEntity();
        Authentication userAuth = new Authentication();
        DialogUtils dialogUtils = new DialogUtils((Activity) context);
        AlertDialog dialogProgress = dialogUtils.dialogProgress("");
        View viewDialog = dialogUtils.getView();
        TextView txtMessage_dialog_progress = viewDialog.findViewById(R.id.txtMessage_dialog_progress);
        if (validateFields()) {
            Snackbar.make(v,
                    "Ops! Ainda há campos a serem preenchidos", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (edtPhone_register.getText().toString().trim().length() > 0 && edtPhone_register.getText().toString().trim().length() < 14) {
            edtPhone_register.setBackgroundResource(R.drawable.bg_edt_global_error);
            cGlobal.onTextListener(edtPassword_register);
            Snackbar.make(v,
                    "Telefone deve seguir o padrão de 11 digitos", Snackbar.LENGTH_LONG).show();
        } else {
            dialogProgress.show();
            txtMessage_dialog_progress.setText("Criando registro");
            if (cityEntities.size() == 0)
                userEntity.setCity(null);
            else
                userEntity.setCity(cityEntities.get(spinnerCity_register.getSelectedItemPosition()).getNome());
            userEntity.setFirstName(edtName_register.getText().toString());
            userEntity.setLastName(edtLastName_register.getText().toString());
            userEntity.setEmail(edtEmail_register.getText().toString());
            userEntity.setPhone(edtPhone_register.getText().toString());
            userEntity.setUf(spinnerUF_register.getSelectedItem().toString());
            userEntity.setCatDefault("Eletrônicos");
            userAuth.register(edtEmail_register.getText().toString().trim(),
                    edtPassword_register.getText().toString().trim(),
                    new AuthCallback() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            UserService userService = new UserService();
                            if (task.isSuccessful()) {
                                txtMessage_dialog_progress.setText("Salvando dados");
                                userEntity.setUid(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                userService.postUser(userEntity, new UserCallback() {
                                    @Override
                                    public void onResponse(Response<UserEntity> response) throws Exception {
                                        if (response.isSuccessful()) {
                                            if (response.body() != null) {
                                                dialogUtils.dialogStatus(v, "Salvando dados",
                                                        "", "success.json", false);
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            sleep(1500);
                                                            MainActivity.statusView = true;
                                                            getActivity().finish();
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
                                        dialogUtils.dialogStatus(v,
                                                "Ops!", messageFailure, "error.json", true);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException w) {
                                    String messageFailure = "Use uma senha com ate 6 digitos!";
                                    dialogUtils.dialogStatus(v,
                                            "Ops!", messageFailure, "error.json", true);
                                } catch (FirebaseAuthUserCollisionException c) {
                                    String messageFailure = "Este email ja esta sendo utilizado!";
                                    dialogUtils.dialogStatus(v,
                                            "Ops!", messageFailure, "error.json", true);

                                } catch (FirebaseException e) {
                                    String messageFailure = "Houve um erro ao processar sua requisição!\n" +
                                            "Verifique sua conexão e tente novamente";
                                    dialogUtils.dialogStatus(v,
                                            "Ops!", messageFailure, "error.json", true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }
                    });
        }
    }

}