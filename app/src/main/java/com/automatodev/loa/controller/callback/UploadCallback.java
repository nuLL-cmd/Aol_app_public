package com.automatodev.loa.controller.callback;

import android.net.Uri;

//Interface de conexão com os metodos de resposta para extender contratos nas chamadas
public interface UploadCallback {
    void onSuccess(Uri uri);
    void onFailure(Exception e);
    void onSuccess();
}
