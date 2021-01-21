package com.automatodev.loa.view.extras;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class RequestPermission  {
    private String[] permissions;
    private ActivityCompat activity;
    private Activity context;

    public RequestPermission(Activity context, String[] permissions) {
        this.context = context;
        this.permissions = permissions;

    }

    public void requestInitialPermission(){
        String[] permissionsD = new String[permissions.length];
        int i= 0;
        for (String permission : permissions){
            if (activity.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED){
                permissionsD[i] = permission;
                i++;
            }
        }

        activity.requestPermissions(context,permissionsD,i);
    }
    public boolean verifyPermissonSingle(String permission) {
        if (activity.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(context, new String[]{permission}, 100);
            return false;
        } else
            return true;

    }
}
