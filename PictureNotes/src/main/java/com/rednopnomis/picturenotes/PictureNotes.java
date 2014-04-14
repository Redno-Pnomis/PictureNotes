package com.rednopnomis.picturenotes;

import com.dropbox.client2.session.Session;

public class PictureNotes {

    public static final int PICTURE_NOTES_DB_VERSION = 1;
    public static final String ACTION_NOTES = "com.rednopnomis.picturenotes.NOTES";
    public static final String ACTION_PHOTOS = "com.rednopnomis.picturenotes.PHOTOS";
    public static final String DROPBOX_APP_KEY = "y87m9gokym4jt45";
    public static final String DROPBOX_APP_SECRET = "ooefum9xpr9xax7";
    public static final String DROPBOX_FOLDER = "PictureNotes";
    public static final String DROPBOX_IMAGES = DROPBOX_FOLDER;
    public static final Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
    public static String sTag = "PictureNotes";
    public static String mDropboxAccessToken;
    public static boolean sTwoPane;

    public enum Database {
        PictureNotes
    }
}
