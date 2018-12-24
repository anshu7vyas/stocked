package com.anshulvyas.csc780.grocerymanagr;


import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Class represents the whole application. Primarily used for instantiating 3rd party library - JodaTimeAndroid
 */
public class StockedApp extends Application {

    /**
     * Called when the activity is starting
     */
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
