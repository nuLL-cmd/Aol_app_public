package com.automatodev.loa.view.extras;

import android.content.Context;
import android.content.SharedPreferences;

import com.automatodev.loa.model.entity.UserEntity;

public class PreferencesApplication {
    private final String PREFS = "myPrefs";
    private Context context;
    private UserEntity userEntity;

    private SharedPreferences sPreferences;
    private SharedPreferences.Editor  sEditor;

    public PreferencesApplication(Context context){
        this.context = context;
        sPreferences = context.getSharedPreferences(PREFS,
                Context.MODE_PRIVATE);

    }


    public UserEntity getUserPrefs(){
        userEntity = new UserEntity();
        userEntity.setFirstName(sPreferences.getString("firstName",null));
        userEntity.setLastName(sPreferences.getString("lastName",null));
        userEntity.setIdUser(sPreferences.getLong("idUser",0));
        userEntity.setUid(sPreferences.getString("uid",null));
        userEntity.setUrlPhoto(sPreferences.getString("urlPhoto",null));
        userEntity.setPhone(sPreferences.getString("phone",null));
        userEntity.setCatDefault(sPreferences.getString("catDefault",null));
        userEntity.setUf(sPreferences.getString("uf",null));;
        userEntity.setCity(sPreferences.getString("city",null));
        userEntity.setEmail(sPreferences.getString("email",null));
        userEntity.setDateSince(sPreferences.getLong("dateSince",0));

        if (userEntity.getIdUser() == 0)
            userEntity = null;

        return userEntity;
    }

    public void setUserPrefs(UserEntity userEntity){
        sEditor = sPreferences.edit();
        sEditor.putLong("idUser",userEntity.getIdUser());
        sEditor.putString("uid",userEntity.getUid());
        sEditor.putString("urlPhoto",userEntity.getUrlPhoto());
        sEditor.putString("phone",userEntity.getPhone());
        sEditor.putString("uf",userEntity.getUf());
        sEditor.putString("city",userEntity.getCity());
        sEditor.putString("catDefault",userEntity.getCatDefault());
        sEditor.putString("firstName",userEntity.getFirstName());
        sEditor.putString("lastName",userEntity.getLastName());
        sEditor.putString("email",userEntity.getEmail());
        sEditor.putLong("dateSince",userEntity.getDateSince());

        sEditor.apply();
    }

}
