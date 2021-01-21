package com.automatodev.loa.view.components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.automatodev.loa.R;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ComponentListener {

    private Context context;
    private ObjectAnimator animator;

    public ComponentListener(Context context) {
        this.context = context;

    }

    //Evento para tratar das mudanças de background dos edits quando necessario
    public void onTextListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0)
                    editText.setBackgroundResource(R.drawable.bg_edt_global);
                else
                    editText.setBackgroundResource(R.drawable.bg_edt_global_error);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public RequestListener<Drawable> glideListener(View view) {
        animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        view.setVisibility(View.VISIBLE);
        RequestListener<Drawable> listener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                animator.start();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                        ;
                        view.setAlpha(1f);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                return false;
            }
        };
        return listener;
    }

    public void fadeComponents(View view, String typeAnimation, float start, float end, long duration, boolean eventActive) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, typeAnimation, start, end);
        animator.setDuration(duration);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (eventActive)
                    view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public void resizeAnimation(View view, int currentWidth, int newWidth) {
        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(500);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */
        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().width = value.intValue();
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }
}
