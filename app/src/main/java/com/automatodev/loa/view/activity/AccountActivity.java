package com.automatodev.loa.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.automatodev.loa.R;
import com.automatodev.loa.databinding.ActivityAccountBinding;
import com.automatodev.loa.view.fragment.LoginFragment;
import com.automatodev.loa.view.fragment.RegisterFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;

    public static boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        //Configura o FragmentAadapter, configura as labels se tiverem e aponta para os fragmentos
        FragmentPagerItemAdapter fragmentAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("", LoginFragment.class)
                        .add("", RegisterFragment.class
                        ).create()
        );
        //Seta o fragementAdapter no viewPager
        binding.viewpager.setAdapter(fragmentAdapter);  
        // Insere os drawables indicadores dos fragmentos
        final LayoutInflater inflater = LayoutInflater.from(this);
        binding.tabLayout.setCustomTabView((container, position, adapter) -> {
            ImageView image = (ImageView) inflater.inflate(R.layout.layout_tab, container, false);
            switch (position) {
                case 0:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_input_24, null));
                    break;
                case 1:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_person_add_24, null));
                    break;
            }
            return image;
        });
        //Seta o viewPager na pagerTab
        binding.tabLayout.setViewPager(binding.viewpager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        status = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        status = false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 100) && resultCode == Activity.RESULT_OK){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(this, "Account id: "+ account.getId(), Toast.LENGTH_SHORT).show();
            }catch (ApiException e){
                e.printStackTrace();
                Log.e("logx","Exception: "+e.getMessage());
            }
        }
    }

    public void actLoginRegister(View view){
        binding.viewpager.setCurrentItem(1);
    }
    public void actRegisterLogin(View view){
        binding.viewpager.setCurrentItem(0);
    }
}