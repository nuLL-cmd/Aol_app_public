package com.automatodev.loa.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.automatodev.loa.databinding.ActivityPhotoBinding;
import com.automatodev.loa.model.entity.ImageEntity;
import com.automatodev.loa.view.adapter.PhotoViewAdapter;

import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    private List<ImageEntity> images;
    private PhotoViewAdapter adapter;
    private ActivityPhotoBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            images = (List<ImageEntity>) bundle.getSerializable("images");
            adapter = new PhotoViewAdapter(this, images);
            binding.viewPagerPhoto.setAdapter(adapter);
        }
        binding.txtProgressPhoto.setText((binding.viewPagerPhoto.getCurrentItem()+1)+" / "+images.size());

        binding.viewPagerPhoto.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            binding.txtProgressPhoto.setText((binding.viewPagerPhoto.getCurrentItem()+1)+" / "+images.size());
        });

        binding.btnClosePhoto.setOnClickListener(vClose -> finish());

        binding.btnNextPhotoPhoto.setOnClickListener(vNext -> {
            if (binding.viewPagerPhoto.getCurrentItem() < images.size())
                binding.viewPagerPhoto.setCurrentItem(binding.viewPagerPhoto.getCurrentItem()+1);
        });

        binding.btnBackPhotoPhoto.setOnClickListener(vBack -> {
            if (binding.viewPagerPhoto.getCurrentItem() !=0)
                binding.viewPagerPhoto.setCurrentItem(binding.viewPagerPhoto.getCurrentItem()-1);
        });
    }


}