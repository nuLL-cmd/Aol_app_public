package com.automatodev.loa.view.components;

import android.animation.Animator;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.airbnb.lottie.LottieAnimationView;
import com.automatodev.loa.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;

public class DialogUtils {

    private Activity context;
    private AlertDialog dialogOperation;
    private View view;
    private SpinKitView spinkitProgress_login;
    private LottieAnimationView animationOk_login;
    private TextView txtMessage_dialog_progress;
    private TextView titleProgress_layout;
    private String type;

    public DialogUtils(Activity context) {
        this.context = context;
    }

    //Funçao para tratar os eventos de animaçaõ dos componentes do dialog de progresso
    public void dialogStatus(View v, String m, String mSnackbar, String animation, boolean snakVisibility) {
        this.animationOk_login.setAnimation(animation);
        if (type.equals("upload"))
            this.txtMessage_dialog_progress.setVisibility(View.GONE);

        if (type.equals("upload"))
            this.titleProgress_layout.setText(m);
        else{
            this.txtMessage_dialog_progress.setText(m);
            this.spinkitProgress_login.setVisibility(View.GONE);
        }

        this.animationOk_login.setVisibility(View.VISIBLE);

        this.animationOk_login.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dialogOperation.dismiss();
                if (snakVisibility)
                    Snackbar.make(v, mSnackbar, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public AlertDialog dialogProgress(String type) {
        this.type = type;
        dialogOperation = new AlertDialog.Builder(context).create();
        this.view = context.getLayoutInflater().inflate(type.equals("upload") ? R.layout.layout_progress_upload : R.layout.layout_dialog_progress, null);
        spinkitProgress_login = view.findViewById(R.id.spinkitProgress_login);
        animationOk_login = view.findViewById(R.id.animationOk_login);
        txtMessage_dialog_progress = view.findViewById(R.id.txtMessage_dialog_progress);
        if (type.equals("upload"))
            titleProgress_layout = view.findViewById(R.id.titleProgress_layout);
        dialogOperation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogOperation.setCancelable(false);
        dialogOperation.setView(view);
        return dialogOperation;
    }

    public View getView() {
        return this.view;
    }

}
