package com.rednopnomis.picturenotes.task;

import android.os.AsyncTask;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.rednokit.RednoKit;
import com.rednokit.controller.base.BaseController;
import com.rednokit.utility.appmsg.AppMsg;
import com.rednopnomis.picturenotes.PictureNotes;
import com.rednopnomis.picturenotes.PictureNotesApplication;
import com.rednopnomis.picturenotes.R;
import com.rednopnomis.picturenotes.model.bll.Note;
import com.squareup.phrase.Phrase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class DropboxUploadTask extends AsyncTask<Void, Void, Boolean> {
    public final static String TAG = DropboxUploadTask.class.getSimpleName();
    private BaseController mController;
    private Note mNote;
    private UploadTaskCallbacks mUploadTaskCallbacks;

    public DropboxUploadTask(BaseController controller, Note note, UploadTaskCallbacks uploadTaskCallbacks) {
        mController = controller;
        mNote = note;
        mUploadTaskCallbacks = uploadTaskCallbacks;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final File tempDir = mController.getCacheDir();
        File tempFile;
        FileWriter fr;
        FileInputStream inputStream;

        try {
            tempFile = File.createTempFile(mNote.getTitle(), ".txt", tempDir);
            fr = new FileWriter(tempFile);
            fr.write(mNote.getText());
            fr.close();
            inputStream = new FileInputStream(tempFile);
        } catch (IOException e) {
            RednoKit.errorLog(TAG, "IO Exception error:", e);
            return false;
        }

        try {
            DropboxAPI.Entry response =
                    PictureNotesApplication.sDBApi.putFile(Phrase.from("{folder}/{filename}.txt")
                                    .put("folder", PictureNotes.DROPBOX_FOLDER)
                                    .put("filename", mNote.getTitle()).format().toString(),
                            inputStream, tempFile.length(), null, null
                    );
            tempFile.delete();
            RednoKit.infoLog("DbExampleLog", "The uploaded file's rev is: " + response.rev);
            mNote.setDropboxRevision(response.rev);
            mNote.setLastUpdated(new Date());
            mNote.update();
            return true;
        } catch (DropboxException e) {
            RednoKit.errorLog(TAG, "File input stream error:", e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            PictureNotesApplication.sendCroutonBroadcast(mController.getString(R.string.uploadSuccessful), AppMsg.STYLE_INFO, mController);
            mUploadTaskCallbacks.restartLoader();
        } else {
            PictureNotesApplication.sendCroutonBroadcast(mController.getString(R.string.noteUploadProblem), AppMsg.STYLE_ALERT, mController);
        }
    }

    public static interface UploadTaskCallbacks {
        public void restartLoader();
    }
}
