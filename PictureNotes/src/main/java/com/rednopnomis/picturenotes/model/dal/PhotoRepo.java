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
import com.rednopnomis.picturenotes.model.bll.Photo;

import java.sql.SQLException;

public class PhotoRepo extends Repo<Photo> {
    public static <Photo, ID> RuntimeExceptionDao<Photo, ID> getPhotoDao() {
        return PictureNotesApplication.sDbHelper.getRednoKitDao(com.rednopnomis.picturenotes.model.bll.Photo.class);
    }

    @Override
    protected <T, ID> RuntimeExceptionDao<T, ID> getRuntimeExceptionDao(Class<T> tClass) {
        return PictureNotesApplication.sDbHelper.getRednoKitDao(Photo.class);
    }

    @Override
    protected String getTag() {
        return PhotoRepo.class.getSimpleName();
    }

    public Photo getPhotoBy_Id(Integer id) {
        return getBy_Id(id, Photo.class);
    }

    public Photo getPhotoById(Integer id) {
        return getById(id, Photo.class);
    }

    public Photo savePhoto(Photo photo) {
        return save(photo, Photo.class);
    }

    public Photo updatePhoto(Photo photo) {
        return update(photo, Photo.class);
    }

    public Boolean deletePhoto(Photo photo) {
        return delete(photo, Photo.class);
    }

    public static class PhotoLoader extends CursorLoader {
        public static final Integer ID = 1;
        private static final String TAG = PhotoLoader.class.getSimpleName();
        private Where<Object, Object> mWhere;
        private OrderBy mOrderBy;

        public PhotoLoader(Context context, Where<Object, Object> where, OrderBy orderBy) {
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
            QueryBuilder<Object, Object> qb = getPhotoDao().queryBuilder();
            try {
                qb.setWhere(mWhere);
                if (mOrderBy != null) {
                    qb.orderBy(mOrderBy.getColumnName(), mOrderBy.isAscending());
                }
                return OrmLiteHelper.getCursor(getPhotoDao().iterator(qb.prepare()));
            } catch (SQLException e) {
                RednoKit.errorLog(TAG, "loadInBackground", e);
            }
            return null;
        }
    }
}
