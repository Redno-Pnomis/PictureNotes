package com.rednopnomis.picturenotes.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rednopnomis.picturenotes.R;
import com.rednopnomis.picturenotes.model.bll.Note;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NoteAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public NoteAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.note_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view, cursor);
            view.setTag(holder);
        }

        holder.title.setText(cursor.getString(holder.titleColumn));
        holder.updateDate.setText(cursor.getString(holder.updateDateColumn));
        holder.text.setText(cursor.getString(holder.textColumn));
        holder.dropboxIcon.setVisibility(TextUtils.isEmpty(cursor.getString(holder.dropboxRevColumn)) ? View.GONE : View.VISIBLE);
    }

    public static class ViewHolder {
        @InjectView(R.id.note_item_title)
        TextView title;
        @InjectView(R.id.note_item_update_date)
        TextView updateDate;
        @InjectView(R.id.note_item_text)
        TextView text;
        @InjectView(R.id.dropbox_icon)
        ImageView dropboxIcon;

        int titleColumn;
        int updateDateColumn;
        int textColumn;
        int dropboxRevColumn;

        public ViewHolder(View view, Cursor cursor) {
            ButterKnife.inject(this, view);
            titleColumn = cursor.getColumnIndexOrThrow(Note.TITLE_COLUMN);
            updateDateColumn = cursor.getColumnIndexOrThrow(Note.LAST_UPDATED_COLUMN);
            textColumn = cursor.getColumnIndexOrThrow(Note.TEXT_COLUMN);
            dropboxRevColumn = cursor.getColumnIndexOrThrow(Note.DROPBOX_REVISION_COLUMN);
        }
    }
}
