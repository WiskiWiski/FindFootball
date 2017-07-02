package online.findfootball.android.firebase.database.children;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.firebase.database.FBDatabase;

/**
 * Created by WiskiW on 08.06.2017.
 * <p>
 * Объект, с возможностью само сохранения, слушанья и удаления в Firebase Database
 * Позволяет обойтись без использования DatabaseLoader для изменения получения объекта из Firebase БД,
 * путем наличия методов save(), load(..) и listen(..)
 */

public abstract class PackableObject extends DatabaseLoader implements DatabasePackable, Parcelable {

    private String path = "";
    private String key = "";

    protected PackableObject() {
    }

    public void setPackablePath(String path) {
        this.path = path;
    }

    @Override
    public String getPackablePath() {
        return path;
    }

    public void setPackableKey(String key) {
        this.key = key;
    }

    @Override
    public String getPackableKey() {
        return key;
    }

    public DataInstanceResult save() {
        return super.save(this);
    }

    @Nullable
    @Override
    public DatabasePackable has(@NonNull DatabasePackable packable) {
        return null;
    }

    public void load(OnLoadListener onLoadListener) {
        this.load(false, onLoadListener);
    }

    public void load(boolean requestNew, OnLoadListener onLoadListener) {
        super.load(this, requestNew, onLoadListener);
    }

    public void listen(OnListenListener onListenListener) {
        super.listen(this, onListenListener);
    }

    public void delete() {
        FBDatabase.getDatabaseReference(this).removeValue();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected PackableObject(Parcel source){
        path = source.readString();
        key = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(key);
    }
}
