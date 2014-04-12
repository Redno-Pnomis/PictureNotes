package com.rednopnomis.picturenotes;

import com.rednokit.RednoKitApplication;
import com.rednopnomis.picturenotes.model.dal.base.PictureNotesDbHelper;

public class PictureNotesApplication extends RednoKitApplication {

    public static PictureNotesDbHelper sDbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppName = PictureNotes.sTag;
        if (sDbHelper == null) {
            sDbHelper = PictureNotesDbHelper.getInstance(this);
        }
    }

    @Override
    public void onTerminate() {
        if (sDbHelper != null) {
            sDbHelper.close();
            sDbHelper = null;
        }
        super.onTerminate();
    }
}
