package com.rednopnomis.picturenotes.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.dropbox.chooser.android.DbxChooser;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.j256.ormlite.stmt.Where;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.rednokit.RednoKit;
import com.rednokit.fragment.SwipeDismissListFragment;
import com.rednokit.model.bll.base.BaseModel;
import com.rednopnomis.picturenotes.PictureNotes;
import com.rednopnomis.picturenotes.PictureNotesApplication;
import com.rednopnomis.picturenotes.R;
import com.rednopnomis.picturenotes.activity.NoteListActivity;
import com.rednopnomis.picturenotes.adapter.NoteAdapter;
import com.rednopnomis.picturenotes.model.bll.Note;
import com.rednopnomis.picturenotes.model.dal.NoteRepo;
import com.rednopnomis.picturenotes.task.DropboxUploadTask;

import java.sql.SQLException;

import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;


/**
 * A list fragment representing a list of Notes. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link NoteDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class NoteListFragment extends SwipeDismissListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = NoteListFragment.class.getSimpleName();
    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static int mSelectedNotePosition;
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int _id) {
        }
    };
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    private boolean mToDownload;
    private DbxChooser mChooser;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteListFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new NoteAdapter(mActivity, null);
        mActivity.getSupportLoaderManager().initLoader(NoteRepo.NoteLoader.ID, null, this);

        //List item animation setup next two lines,
        //see: https://github.com/nhaarman/ListViewAnimations/wiki/Appearance-Animations
        AlphaInAnimationAdapter aiaAdapter = new AlphaInAnimationAdapter(mAdapter);
        aiaAdapter.setAbsListView(getListView());

        setListAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
        init(new OnRefreshListener() {
                 @Override
                 public void onRefreshStarted(View view) {
                     //Go get data from server here, when data returns set the layout to refreshed
                     mActivity.getSupportLoaderManager()
                             .restartLoader(NoteRepo.NoteLoader.ID, null, NoteListFragment.this);
                 }
             },
                //You can add options here for the pull to refresh action bar
                Options.create().build(),
                //The class this list is going to deal with
                Note.class,
                //What happens when you swipe to dismiss an item
                new DismissListener() {
                    @Override
                    public void onDismiss(int _id) {
                        //item is hid in parent class, thus we only need to restart the loader here.
                        mActivity.getSupportLoaderManager()
                                .restartLoader(NoteRepo.NoteLoader.ID, null, NoteListFragment.this);
                        final Note note = Note.getBy_Id(_id);
                        mActivity.makeUndoToast(note.getTitle() + " was hidden",
                                new OnClickWrapper(TAG,
                                        new SuperToast.OnClickListener() {
                                            @Override
                                            public void onClick(View view, Parcelable parcelable) {
                                                note.unHide();
                                                mActivity.getSupportLoaderManager()
                                                        .restartLoader(NoteRepo.NoteLoader.ID, null, NoteListFragment.this);
                                            }
                                        }
                                )
                        );

                    }
                }
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mChooser = new DbxChooser(PictureNotes.DROPBOX_APP_KEY);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PictureNotesApplication.sDBApi.getSession().authenticationSuccessful()) {
            try {
                PictureNotesApplication.sDBApi.getSession().finishAuthentication();
                PictureNotes.mDropboxAccessToken = PictureNotesApplication.sDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                RednoKit.errorLog("DbAuthLog", "Error authenticating", e);
            }
        }
        registerForContextMenu(getListView());
        if (mSelectedNotePosition != 0) {
            uploadNote(mSelectedNotePosition);
        }
        if (mToDownload) {
            downloadNote();
        }
    }

    @Override
    public void onPause() {
        unregisterForContextMenu(getListView());
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_note:
                mCallbacks.onItemSelected(0);
                return true;
            case R.id.note_download:
                downloadNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mActivity.getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_upload:
                uploadNote(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void uploadNote(int id) {
        if (PictureNotes.mDropboxAccessToken == null) {
            mSelectedNotePosition = id;
            doDropboxAuth();
            return;
        }

        Cursor cursor = (Cursor) mAdapter.getItem(mSelectedNotePosition);
        if (cursor != null) {

            DropboxUploadTask upload = new DropboxUploadTask(mActivity,
                    Note.getBy_Id(cursor.getInt(cursor.getColumnIndex(BaseModel._ID_COLUMN))),
                    new DropboxUploadTask.UploadTaskCallbacks() {
                        @Override
                        public void restartLoader() {
                            mActivity.getSupportLoaderManager()
                                    .restartLoader(NoteRepo.NoteLoader.ID, null, NoteListFragment.this);
                            mSelectedNotePosition = 0;
                        }
                    }
            );
            upload.execute();
        }

    }

    private void downloadNote() {
        if (PictureNotes.mDropboxAccessToken == null) {
            mToDownload = true;
            doDropboxAuth();
            return;
        }

        mChooser.forResultType(DbxChooser.ResultType.FILE_CONTENT)
                .launch(mActivity, NoteListActivity.DBX_CHOOSER_REQUEST);
        mToDownload = false;
    }

    private void doDropboxAuth() {
        if (!PictureNotesApplication.sDBApi.getSession().authenticationSuccessful()) {
            PictureNotesApplication.sDBApi.getSession().startOAuth2Authentication(mActivity);
        }
    }


    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Cursor cursor = (Cursor) mAdapter.getItem(position);
        if (cursor != null) {
            mCallbacks.onItemSelected(cursor.getInt(cursor.getColumnIndex(BaseModel._ID_COLUMN)));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Where<Object, Object> noteWhere = NoteRepo.getNoteDao().queryBuilder().where();
        try {
            noteWhere.eq(Note.HIDDEN_COLUMN, false);
        } catch (SQLException e) {
            RednoKit.errorLog(TAG, "onCreateLoader", e);
        }
        return new NoteRepo.NoteLoader(mActivity, noteWhere, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        mPullToRefreshLayout.setRefreshComplete();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }
    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int _id);
    }
}
