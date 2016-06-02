package com.companyname.ceramicgod;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by David on 6/2/2016.
 */
public class MultiDexApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
