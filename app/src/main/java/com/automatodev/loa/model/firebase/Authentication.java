package com.automatodev.loa.model.firebase;

import androidx.annotation.NonNull;

import com.automatodev.loa.controller.callback.AuthCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Classe model que fara a conexão direta com o firebase afim de authenticar o usuario
public class Authentication {

    private FirebaseAuth firebaseAuth;

    //Construtor que inici um objeto do tipo FirebaseAuth.
    public Authentication() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Metodo de login, recebe email e senha como parametro, e um callback pra a chamada assincrona
    public void login(String email, String password, final AuthCallback authCallback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(authCallback::onComplete).addOnFailureListener(authCallback::onFailure);
    }

    //Metodo de Registro, recebe email e senha como parametro, e um callback pra a chamada assincrona
    public void register(String email, String password, final AuthCallback authCallback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        authCallback.onComplete(task);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                authCallback.onFailure(e);

            }
        });
    }

    //Metodo que retorna um objeto FirebaseUser
    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    //Metodo que faz logout quando chamado
    public void userSingOut() {
        firebaseAuth.signOut();
    }

}
