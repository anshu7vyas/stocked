package com.anshulvyas.csc780.grocerymanagr;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by av7 on 12/2/15.
 */
public class FooDeDApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
