package com.automatodev.loa.controller.callback;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

//Interface de conexão com os metodos de resposta para extender contratos nas chamadas
public interface AuthCallback {

    void onComplete(Task<AuthResult> task);
    void onFailure(Exception e);

}
