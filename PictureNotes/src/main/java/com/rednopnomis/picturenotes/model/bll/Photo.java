package com.rednopnomis.picturenotes.model.bll;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rednokit.interfaces.CursorLoadedListItem;
import com.rednokit.model.bll.base.BaseModel;
import com.rednopnomis.picturenotes.model.dal.base.PictureNotesDbHelper;

@DatabaseTable(tableName = PictureNotesDbHelper.PHOTO_TABLE)
public class Photo extends BaseModel implements CursorLoadedListItem {

    public static final String FILE_NAME_COLUMN = "fileName";
    private static final long serialVersionUID = -7799804466886021261L;

    @DatabaseField(columnName = FILE_NAME_COLUMN, canBeNull = false)
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void click(int i) {

    }

    @Override
    public boolean hide(int i) {
        return false;
    }
}
