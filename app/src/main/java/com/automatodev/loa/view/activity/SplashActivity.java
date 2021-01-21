package com.automatodev.loa.view.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.automatodev.loa.R;
import com.automatodev.loa.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private LottieAnimationView okAnimation_progress;
    private Animation animTitleLogo;
    private Animation animLogoAol;
    private RelativeLayout relativeLayout;
    private ActivitySplashBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Area para instanciação de elementos e objetos
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        okAnimation_progress = findViewById(R.id.okAnimation_progress);
        relativeLayout = findViewById(R.id.relativeLogo_splash);

        animTitleLogo = AnimationUtils.loadAnimation(this, R.anim.anim_fade);
        animLogoAol = AnimationUtils.loadAnimation(this, R.anim.push_left);


        //Area para chamada de metodos ao iniciar a activity
        okAnimation_progress.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                okAnimation_progress.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.setAnimation(animLogoAol);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        getInfoPackage(binding.lblVersionSplash);
        splashAnimation();

    }

    //Metodo que cria uma nova thread para aguardar o termino da animação e fade da logo do programa
    public void splashAnimation(){
        SharedPreferences initalData = getSharedPreferences("initData", Context.MODE_PRIVATE);
        boolean havConfig = initalData.getBoolean("havConfig", false);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(5700);
                    if (havConfig)
                         startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    else
                         startActivity(new Intent(SplashActivity.this, LocationActivity.class));
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
