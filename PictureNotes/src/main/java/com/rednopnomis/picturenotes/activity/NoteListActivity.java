package com.rednopnomis.picturenotes.activity;

import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.dropbox.chooser.android.DbxChooser;
import com.rednokit.RednoKit;
import com.rednokit.controller.base.BaseController;
import com.rednokit.utility.appmsg.AppMsg;
import com.rednopnomis.picturenotes.R;
import com.rednopnomis.picturenotes.fragment.NoteDetailFragment;
import com.rednopnomis.picturenotes.fragment.NoteListFragment;
import com.rednopnomis.picturenotes.model.bll.Note;
import com.rednopnomis.picturenotes.model.dal.NoteRepo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


/**
 * An activity representing a list of Notes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NoteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.rednopnomis.picturenotes.fragment.NoteListFragment} and the item details
 * (if present) is a {@link com.rednopnomis.picturenotes.fragment.NoteDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link com.rednopnomis.picturenotes.fragment.NoteListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class NoteListActivity extends BaseController
        implements NoteListFragment.Callbacks {

    public static final int DBX_CHOOSER_REQUEST = 0;  // You can change this if needed
    public static final String TAG = NoteListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private NoteListFragment mNoteListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setContentView(R.layout.activity_note_list);
        mNoteListFragment = ((NoteListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.note_list));

        if (findViewById(R.id.note_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.

            mNoteListFragment.setActivateOnItemClick(true);
        }

    }

    /**
     * Callback method from {@link NoteListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int _id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(NoteDetailFragment.ARG_ITEM_ID, _id);
            NoteDetailFragment fragment = new NoteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.note_detail_container, fragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, NoteDetailActivity.class);
            detailIntent.putExtra(NoteDetailFragment.ARG_ITEM_ID, _id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DBX_CHOOSER_REQUEST) {
            if (resultCode == RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);
                Note note = new Note();
                File file = new File(result.getLink().getPath());
                String[] parts = result.getName().split(".");
                if (!parts[1].equalsIgnoreCase("txt")) {
                    makeCrouton(getString(R.string.mustHaveTxtExtension), AppMsg.STYLE_ALERT);
                    return;
                }
                note.setTitle(parts[0]);
                InputStream inputStream = null;
                String text = "";
                try {
                    //f = new BufferedInputStream(new FileInputStream(filePath));
                    //f.read(buffer);

                    inputStream = new FileInputStream(file);
                    char current;
                    while (inputStream.available() > 0) {
                        current = (char) inputStream.read();
                        text = text + String.valueOf(current);

                    }

                } catch (Exception e) {
                    RednoKit.debugLog(TAG, e.toString());
                } finally {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                        } catch (IOException ignored) {
                        }
                }
                note.setText(text);
                note.setLastUpdated(new Date());
                note.setDropboxRevision(result.getName());
                note.save();
                if (mNoteListFragment != null) {
                    getSupportLoaderManager()
                            .restartLoader(NoteRepo.NoteLoader.ID, null, mNoteListFragment);
                }
                makeCrouton(getString(R.string.noteSaved), AppMsg.STYLE_INFO);
            } else {
                makeCrouton(getString(R.string.noFilesDownloaded), AppMsg.STYLE_INFO);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
