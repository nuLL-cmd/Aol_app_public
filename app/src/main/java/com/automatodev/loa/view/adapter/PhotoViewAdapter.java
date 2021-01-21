package com.automatodev.loa.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.automatodev.loa.model.entity.ImageEntity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class PhotoViewAdapter extends PagerAdapter {

    private Context context;
    private List<ImageEntity> images;
    // private  int[] drawable = new int[]{R.drawable.ic_new_user, R.drawable.ic_facebook_blue, R.drawable.ic_linkedin};

    public PhotoViewAdapter(Context context, List<ImageEntity> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView imageView = new PhotoView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        Glide.with(context).load(images.get(position).getUrlImage()).transition(new DrawableTransitionOptions().crossFade()).into(imageView);
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


}
