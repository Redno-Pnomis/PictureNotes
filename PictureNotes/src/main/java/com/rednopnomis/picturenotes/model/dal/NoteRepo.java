package com.rednopnomis.picturenotes.model.dal;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.stmt.query.OrderBy;
import com.rednokit.RednoKit;
import com.rednokit.model.dal.base.Repo;
import com.rednokit.model.dal.utility.OrmLiteHelper;
import com.rednopnomis.picturenotes.PictureNotesApplication;
import com.rednopnomis.picturenotes.model.bll.Note;

import java.sql.SQLException;

public class NoteRepo extends Repo<Note> {
    public static <Note, ID> RuntimeExceptionDao<Note, ID> getNoteDao() {
        return PictureNotesApplication.sDbHelper.getRednoKitDao(com.rednopnomis.picturenotes.model.bll.Note.class);
    }

    @Override
    protected <T, ID> RuntimeExceptionDao<T, ID> getRuntimeExceptionDao(Class<T> tClass) {
        return PictureNotesApplication.sDbHelper.getRednoKitDao(Note.class);
    }

    @Override
    protected String getTag() {
        return NoteRepo.class.getSimpleName();
    }

    public Note getNoteBy_Id(Integer id) {
        return getBy_Id(id, Note.class);
    }

    public Note getNoteById(Integer id) {
        return getById(id, Note.class);
    }

    public Note saveNote(Note note) {
        return save(note, Note.class);
    }

    public Note updateNote(Note note) {
        return update(note, Note.class);
    }

    public boolean saveOrUpdateNote(Note note) {
        return saveOrUpdateById(note, Note.class);
    }

    public Boolean deleteNote(Note note) {
        return delete(note, Note.class);
    }

    public static class NoteLoader extends CursorLoader {
        public static final Integer ID = 1;
        private static final String TAG = NoteLoader.class.getSimpleName();
        private Where<Object, Object> mWhere;
        private OrderBy mOrderBy;

        public NoteLoader(Context context, Where<Object, Object> where, OrderBy orderBy) {
            super(context);
            mWhere = where;
            mOrderBy = orderBy;
        }

        public void setWhere(Where<Object, Object> where) {
            mWhere = where;
        }

        public void setOrder(OrderBy orderBy) {
            mOrderBy = orderBy;
        }

        @Override
        public Cursor loadInBackground() {
            // build your query
            QueryBuilder<Object, Object> qb = getNoteDao().queryBuilder();
            try {
                qb.setWhere(mWhere);
                if (mOrderBy != null) {
                    qb.orderBy(mOrderBy.getColumnName(), mOrderBy.isAscending());
                }
                return OrmLiteHelper.getCursor(getNoteDao().iterator(qb.prepare()));
            } catch (SQLException e) {
                RednoKit.errorLog(TAG, "loadInBackground", e);
            }
            return null;
        }
    }
}
