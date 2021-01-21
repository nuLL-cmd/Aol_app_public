package com.automatodev.loa.view.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.automatodev.loa.R;
import com.automatodev.loa.view.components.ComponentListener;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    private String FACEBOOK_URL = "https://www.facebook.com/Nazomiiamazo";
    private String LINKEDIN_URL = "https://www.linkedin.com/in/marcoaj/";
    private String INSTAGRAM_URL = "https://www.instagram.com/automatodev/";

    private ImageView imgFacebook_about;
    private ImageView  imgInstagran_about;
    private ImageView imgLinkedin_about;
    private ImageView imgLogo_about;
    private TextView txtVersao_about;
    public static  boolean status;

    ComponentListener componentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        componentListener = new ComponentListener(this);
        imgFacebook_about =  findViewById(R.id.imgFacebook_about);
        imgInstagran_about =  findViewById(R.id.imgInstagram_about);
        imgLinkedin_about =  findViewById(R.id.imgLinkedin_about);
        txtVersao_about =  findViewById(R.id.txtVersao_about);
        imgLogo_about = findViewById(R.id.imgLogo_about);
        componentListener.fadeComponents(imgLogo_about,"alpha",0f,1f,2000,false);

        imgFacebook_about.setOnClickListener(this);
        imgInstagran_about.setOnClickListener(this);
        imgLinkedin_about.setOnClickListener(this);

        getInfoPackage(txtVersao_about);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgFacebook_about:
                openFacebook();
                break;
            case R.id.imgInstagram_about:
                openInstagram();
                break;
            case R.id.imgLinkedin_about:
                openLinkedin();
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        status = true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        status = false;
    }

    //Metodo para abrir a pagina do dev no facebook
    public void openFacebook(){
        Intent facebook = new Intent(Intent.ACTION_VIEW);
        facebook.setData(Uri.parse(FACEBOOK_URL));
        startActivity(facebook);
    }
    //Metodo para abrir a pagina do dev no linkedin
    public void openLinkedin(){
        Intent linkedin = new Intent(Intent.ACTION_VIEW);
        linkedin.setData(Uri.parse(LINKEDIN_URL));
        startActivity(linkedin);
    }
    //Metodo para abrir a pagina do dev no instagram
    public void openInstagram(){
        Intent instagram = new Intent(Intent.ACTION_VIEW);
        instagram.setData(Uri.parse(INSTAGRAM_URL));
        startActivity(instagram);
    }

    public void actAbutParent(View view){
        NavUtils.navigateUpFromSameTask(this);
    }

    public void getInfoPackage(TextView txtVersion){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (info != null)
                txtVersion.setText("Versão "+info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}