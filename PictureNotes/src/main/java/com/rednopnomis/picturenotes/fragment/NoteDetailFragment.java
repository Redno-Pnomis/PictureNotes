package com.rednopnomis.picturenotes.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.rednokit.fragment.base.BaseFragment;
import com.rednokit.utility.appmsg.AppMsg;
import com.rednopnomis.picturenotes.PictureNotes;
import com.rednopnomis.picturenotes.PictureNotesApplication;
import com.rednopnomis.picturenotes.R;
import com.rednopnomis.picturenotes.activity.NoteListActivity;
import com.rednopnomis.picturenotes.model.bll.Note;

import java.util.Date;

/**
 * A fragment representing a single Note detail screen.
 * This fragment is either contained in a {@link com.rednopnomis.picturenotes.activity.NoteListActivity}
 * in two-pane mode (on tablets) or a {@link com.rednopnomis.picturenotes.activity.NoteDetailActivity}
 * on handsets.
 */
public class NoteDetailFragment extends BaseFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    @InjectView(R.id.note_title)
    protected EditText mTitle;
    @InjectView(R.id.note_text)
    protected EditText mText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    /**
     * The content this fragment is presenting.
     */
    private Note mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteDetailFragment() {
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSaved() {
        }
    };
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();

        if (bundle.containsKey(ARG_ITEM_ID) && bundle.getInt(ARG_ITEM_ID) > 0) {
            mItem = Note.getBy_Id(getArguments().getInt(ARG_ITEM_ID));
        } else {
            mItem = new Note();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_detail, container, false);
        ButterKnife.inject(this, rootView);

        mTitle.setText(mItem.getTitle());
        mText.setText(mItem.getText());

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveNote() {
        if (TextUtils.isEmpty(mTitle.getText()) || TextUtils.isEmpty(mText.getText())) {
            PictureNotesApplication.sendCroutonBroadcast(mActivity.getString(R.string.titleAndTextRequired), AppMsg.STYLE_ALERT, mActivity);
        } else {
            mItem.setTitle(mTitle.getText().toString());
            mItem.setText(mText.getText().toString());
            mItem.setLastUpdated(new Date());
            if (mItem.saveOrUpdate()) {
                if(!PictureNotes.sTwoPane){
                    NavUtils.navigateUpTo(mActivity, new Intent(mActivity,
                            NoteListActivity.class));
                } else {
                    mCallbacks.onItemSaved();
                }
                PictureNotesApplication.sendCroutonBroadcast(mActivity.getString(R.string.noteSaved), AppMsg.STYLE_INFO, mActivity, 500);
            } else {
                PictureNotesApplication.sendCroutonBroadcast(mActivity.getString(R.string.problemSaving), AppMsg.STYLE_ALERT, mActivity);
            }
        }
    }

    public interface Callbacks {
        /**
         * Callback for when an item has been saved.
         */
        public void onItemSaved();
    }
}
