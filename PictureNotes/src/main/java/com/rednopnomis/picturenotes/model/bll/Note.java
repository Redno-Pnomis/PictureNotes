package com.rednopnomis.picturenotes.model.bll;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.rednokit.interfaces.CursorLoadedListItem;
import com.rednokit.model.bll.base.BaseModel;
import com.rednopnomis.picturenotes.model.dal.NoteRepo;
import com.rednopnomis.picturenotes.model.dal.base.PictureNotesDbHelper;

import java.util.Date;

@DatabaseTable(tableName = PictureNotesDbHelper.NOTE_TABLE)
public class Note extends BaseModel implements CursorLoadedListItem {
    public static final String TITLE_COLUMN = "title";
    public static final String LAST_UPDATED_COLUMN = "lastUpdated";
    public static final String TEXT_COLUMN = "text";
    public static final String HIDDEN_COLUMN = "hidden";
    private static final long serialVersionUID = -2436359145550172003L;
    @DatabaseField(columnName = TITLE_COLUMN, canBeNull = false)
    private String title;
    @DatabaseField(columnName = LAST_UPDATED_COLUMN, canBeNull = false)
    private Date lastUpdated;
    @DatabaseField(columnName = TEXT_COLUMN, canBeNull = false)
    private String text;
    @DatabaseField(columnName = HIDDEN_COLUMN, canBeNull = false, defaultValue = "0")
    private Boolean hidden;

    public static Note getById(int id) {
        return new NoteRepo().getNoteById(id);
    }

    public static Note getBy_Id(int _id) {
        return new NoteRepo().getNoteBy_Id(_id);
    }

    public String getTitle() {
        return (title != null) ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getText() {
        return (text != null) ? text : "";
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public boolean hide() {
        hidden = true;
        return update().isHidden();
    }

    public boolean unHide() {
        hidden = false;
        return !update().isHidden();
    }

    public Note save() {
        return new NoteRepo().saveNote(this);
    }

    public Note update() {
        return new NoteRepo().updateNote(this);
    }

    public boolean saveOrUpdate() {
        return new NoteRepo().saveOrUpdateNote(this);
    }

    @Override
    public void click(int i) {
        //Do something
    }

    @Override
    public boolean hide(int i) {
        return getBy_Id(i).hide();
    }
}
