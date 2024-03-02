package com.alex.edudus;

import android.app.Application;
import android.content.Context;

public class AppController extends Application {
    private static AppController instance;
    public static Context context;
    public static AppController get() { return instance; }
	public static AppController getInstance() { return instance; }

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
        context = getApplicationContext();
        instance = this;

	}
	
	public void onTerminate() {
		super.onTerminate();
	};

}
