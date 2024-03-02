package com.alex.edudus.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.afpensdk.BuildConfig;

public class LogUtils {

    public static void d(String tag,String message){
        if (BuildConfig.DEBUG) {
            Log.d(tag, "=======> " + message);
        }
    }
    public static void e(String tag,String message,Exception e){
            Log.e(tag, "=======> " + message,e);
    }

    public static void i(String tag,String message){
            Log.i(tag, "=======> " + message);
    }

    public static void ToastMessage(Context context, String message, boolean isShort){
        Toast.makeText( context, message, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG  ).show();
    }
}
