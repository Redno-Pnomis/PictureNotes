package com.rednopnomis.picturenotes.model.dal.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rednokit.RednoKit;
import com.rednokit.model.dal.base.DbHelper;
import com.rednopnomis.picturenotes.PictureNotes;
import com.rednopnomis.picturenotes.PictureNotesApplication;
import com.rednopnomis.picturenotes.model.bll.Note;
import com.rednopnomis.picturenotes.model.bll.Photo;

import java.sql.SQLException;

public class PictureNotesDbHelper extends DbHelper {
    public static final String NOTE_TABLE = "Note";
    public static final String PHOTO_TABLE = "Photo";
    private static PictureNotesDbHelper helperInstance;

    private PictureNotesDbHelper(Context context) {
        super(context, PictureNotes.Database.PictureNotes.name(),
                PictureNotes.PICTURE_NOTES_DB_VERSION);
    }

    public static PictureNotesDbHelper getInstance(PictureNotesApplication application) {
        if (helperInstance == null)
            helperInstance = new PictureNotesDbHelper(application);
        return helperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        dropTables();
    }

    public void resetDB() {
        clearTables();
    }

    private void clearTables() {
        try {
            TableUtils.clearTable(connectionSource, Note.class);
            TableUtils.clearTable(connectionSource, Photo.class);
        } catch (SQLException e) {
            RednoKit.errorLog(TAG, "Problem creating " + PictureNotesApplication.sAppName + " DB tables.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void createTables() {
        try {
            TableUtils.createTableIfNotExists(connectionSource, Note.class);
            TableUtils.createTableIfNotExists(connectionSource, Photo.class);
        } catch (SQLException e) {
            RednoKit.errorLog(TAG, "Problem creating " + PictureNotesApplication.sAppName + " DB tables.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void dropTables() {
        try {
            TableUtils.dropTable(connectionSource, Note.class, true);
            TableUtils.dropTable(connectionSource, Photo.class, true);
        } catch (SQLException e) {
            RednoKit.errorLog(TAG, "Problem dropping " + PictureNotesApplication.sAppName + " DB tables.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
