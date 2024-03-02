package com.alex.edudus.utility;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.alex.edudus.AppController;


public class Glo {
    private static Glo instance;

    public int lineWidthLv = 2;
    public int widthPixels;
    public int heightPixels;
    public boolean bEnableShowDotLog;
    public static Glo getInstance() {
        if (instance == null) {
            instance = new Glo();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppController.context);
            instance.lineWidthLv = Integer.parseInt(pref.getString("DotSizeSetting","3"));
            instance.widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
            instance.heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;
            instance.bEnableShowDotLog = pref.getBoolean("EnableShowDotLog",false);

        }
        return instance;
    }
    public float lineWidthByLevel_PressureUse(float lvl){
        float dotsize = 6;
        if(lvl == 1)
            dotsize = 4;
        else if(lvl == 2)
            dotsize = 5;
        else if(lvl == 3)
            dotsize = 6;
        else if(lvl == 4)
            dotsize = 8;
        else if(lvl == 5)
            dotsize = 10;
        return dotsize;
    }
    public float lineWidthByLevel2(float lvl){
        float w;
        w = widthPixels*0.0025f;

        float outw = w;
        if (lvl == 0)
            outw = w * 0.6f;
        else if (lvl == 1)
            outw = w * 0.8f;
        else if (lvl == 2)
            outw = w * 1f;
        else if (lvl == 3)
            outw = w * 1.3f;
        else if (lvl == 4)
            outw = w * 1.5f;
        return outw;
    }
}
