package com.automatodev.loa.model.firebase;

import android.net.Uri;

import com.automatodev.loa.controller.callback.UploadCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//Classe utilitaria com alguns metodos usados para storage  - Firebase
public class StorageTools {
    private StorageReference userStorage;


    public void uploadPhotoUser(String path, String uid, Uri uri, final UploadCallback callback) {
        userStorage = FirebaseStorage.getInstance().getReference(path).child(uid);
        userStorage.putFile(uri).addOnSuccessListener(taskSnapshot -> userStorage.getDownloadUrl()
                .addOnSuccessListener(callback::onSuccess))
                .addOnFailureListener(callback::onFailure);
    }

}
