package com.rednopnomis.picturenotes;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.rednokit.RednoKitApplication;
import com.rednopnomis.picturenotes.model.dal.base.PictureNotesDbHelper;

public class PictureNotesApplication extends RednoKitApplication {

    public static PictureNotesDbHelper sDbHelper;
    public static DropboxAPI<AndroidAuthSession> sDBApi;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppName = PictureNotes.sTag;
        if (sDbHelper == null) {
            sDbHelper = PictureNotesDbHelper.getInstance(this);
        }
        if (sDBApi == null) {
            AppKeyPair appKeys = new AppKeyPair(PictureNotes.DROPBOX_APP_KEY, PictureNotes.DROPBOX_APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(appKeys, PictureNotes.ACCESS_TYPE);
            sDBApi = new DropboxAPI<>(session);
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
