package com.enernet.eg;

import android.app.Activity;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class Utils {
    public enum StatusBarColorType {
        BLACK_STATUS_BAR( R.color.black ),
        MAIN_STATUS_BAR( R.color.eg_cyan_light);


        private int backgroundColorId;

        StatusBarColorType(int backgroundColorId){
            this.backgroundColorId = backgroundColorId;
        }

        public int getBackgroundColorId() {
            return backgroundColorId;
        }
    }

    public static void setStatusBarColor(Activity activity, StatusBarColorType colorType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, colorType.getBackgroundColorId()));
        }
    }
}