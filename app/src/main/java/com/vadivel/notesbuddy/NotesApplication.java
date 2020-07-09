package com.vadivel.notesbuddy;

import android.app.Application;

public class NotesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

    /*    if (BuildConfig.DEBUG) {
            if (com.squareup.leakcanary.LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            com.squareup.leakcanary.LeakCanary.install(this);
        }*/
    }
}
