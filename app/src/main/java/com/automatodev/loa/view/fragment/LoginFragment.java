package com.automatodev.loa.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.automatodev.loa.R;
import com.automatodev.loa.controller.callback.AuthCallback;
import com.automatodev.loa.model.firebase.Authentication;
import com.automatodev.loa.view.activity.MainActivity;
import com.automatodev.loa.view.components.ComponentListener;
import com.automatodev.loa.view.components.DialogUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String email;

    private Authentication authentication;
    private ComponentListener cListener;
    private DialogUtils dialogUtils;
    private AlertDialog dialogProgress;
    private View viewParent;
    private View viewProgress;
    private Context context;
    private SignInButton buttonGoogle;

    private EditText edtEmailLogin;
    private EditText edtPasswordLogin;
    private ImageButton btnIr_login;
    private Button btnForget_login;
    private TextView txtMessage_dialog_progress;
    private GoogleSignInClient signClient;
    private RelativeLayout relativeDaddy_login;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cListener = new ComponentListener(context);
        authentication = new Authentication();
        dialogUtils = new DialogUtils((Activity) context);
        GoogleSignInOptions singOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.clientId))
                .requestEmail()
                .build();

        signClient = GoogleSignIn.getClient(context, singOption);


    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewParent = inflater.inflate(R.layout.fragment_login, container, false);

        edtEmailLogin = viewParent.findViewById(R.id.edtEmail_login);
        edtPasswordLogin = viewParent.findViewById(R.id.edtPassword_login);
        btnIr_login = viewParent.findViewById(R.id.btnIr_login);
        btnForget_login = viewParent.findViewById(R.id.btnForget_login);
        buttonGoogle = viewParent.findViewById(R.id.btnGoogle_login);
        relativeDaddy_login = viewParent.findViewById(R.id.relativeDaddy_login);
        btnIr_login.setOnClickListener(this);
        btnForget_login.setOnClickListener(this);
        buttonGoogle.setOnClickListener(this);
        setGooglePlusButtonText(buttonGoogle, "Fazer login com o Google");
        btnIr_login.setTooltipText("Fazer");
        return viewParent;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnIr_login:
                actLoginMain();
                break;
            case R.id.btnForget_login:
                recoveryPassword();
                break;
            case R.id.btnGoogle_login:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signIntent = signClient.getSignInIntent();
        startActivityForResult(signIntent,100);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 100) && resultCode == Activity.RESULT_OK){
            dialogProgress = dialogUtils.dialogProgress("");
            viewProgress = dialogUtils.getView();
            txtMessage_dialog_progress = viewProgress.findViewById(R.id.txtMessage_dialog_progress);
            txtMessage_dialog_progress.setText("Quase lá!");
            dialogProgress.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loginWithAccountGoogle(account.getIdToken());
            }catch (ApiException e){
                e.printStackTrace();
                Log.e("logx","Exception: "+e.getMessage());
            }
        }
    }

    private void loginWithAccountGoogle(String idToken) {

        AuthCredential cred = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(cred)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        dialogUtils.dialogStatus(viewParent, "Tudo certo!", "", "success.json", false);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    sleep(1200);
                                    MainActivity.statusView = true;
                                    getActivity().finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    //Autentica o usuario para dando espaço para a activity principal caso tudo corra dentro do fluxo
    public void actLoginMain() {
        if (validateFields()) {
            Snackbar.make(viewParent,
                    "Ops! Ainda há campos a serem preenchidos", Snackbar.LENGTH_LONG).show();
            return;
        }
        dialogProgress = dialogUtils.dialogProgress("");
        viewProgress = dialogUtils.getView();
        txtMessage_dialog_progress = viewProgress.findViewById(R.id.txtMessage_dialog_progress);
        txtMessage_dialog_progress.setText("Quase lá!");
        dialogProgress.show();
        String email = edtEmailLogin.getText().toString().trim();
        String password = edtPasswordLogin.getText().toString().trim();
        authentication.login(email, password, new AuthCallback() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    dialogUtils.dialogStatus(viewParent, "Tudo certo!", "", "success.json", false);
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sleep(1200);
                                MainActivity.statusView = true;
                                getActivity().finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        String message = "Email ou senha invalidos!";
                        dialogUtils.dialogStatus(viewParent, "Ops!", message, "error.json", true);
                    } catch (FirebaseAuthEmailException e) {
                        String message = "Email com formato invalido!";
                        dialogUtils.dialogStatus(viewParent, "Ops!", message, "error.json", true);
                    } catch (Exception e) {
                        String message = "Algo deu errado, verifique seu email e/ou senha.\nDa uma olhada na sua conexão :D";
                        dialogUtils.dialogStatus(viewParent, "Ops!", message, "error.json", true);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("logx", "Msg: " + e.getMessage());
            }
        });
    }

    //Metodo que valida os campos de texto colocando efeito caso os mesmos estajam vazios e informando ao usuario
    private boolean validateFields() {
        int count = 0;
        EditText[] fields = new EditText[2];
        fields[0] = edtEmailLogin;
        fields[1] = edtPasswordLogin;
        for (EditText e : fields) {
            if (e.getText().toString().trim().isEmpty()) {
                e.setBackgroundResource(R.drawable.bg_edt_global_error);
                cListener.onTextListener(e);
                count++;
            }
        }
        return count != 0;
    }

    public void recoveryPassword(){
        ProgressDialog progress =  new ProgressDialog(context);
        progress.setTitle("Aguarde");
        progress.setMessage("Enviando email...");
        AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View viewRecovery = getLayoutInflater().inflate(R.layout.layout_recovery, null);
        EditText edtEmail_recovery = viewRecovery.findViewById(R.id.edtEmail_recovery);
        Button btnSend_recovery = viewRecovery.findViewById(R.id.btnSend_recovery);
        alert.setView(viewRecovery);
        alert.show();

        btnSend_recovery.setOnClickListener(v ->{
            if(!edtEmail_recovery.getText().toString().trim().isEmpty()){
                email = edtEmail_recovery.getText().toString().trim();
                alert.dismiss();
                progress.show();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                progress.dismiss();
                                Snackbar.make(relativeDaddy_login,
                                        "Sucesso! Um email de recuperação foi enviado para "+email, Snackbar.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(e -> {
                            progress.dismiss();;
                            Snackbar.make(relativeDaddy_login,
                                    "Ops! Email não encontrado! \nVerifique se o email está correto e tente novamente.", Snackbar.LENGTH_LONG).show();
                        });
            }else{
                edtEmail_recovery.setBackgroundResource(R.drawable.bg_edt_global_error);
                cListener.onTextListener(edtEmail_recovery);
                Toast.makeText(context, "Campo não pode ser vazio!",Toast.LENGTH_LONG).show();
            }
        });

    }

}