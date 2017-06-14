package online.findfootball.android.firebase.database.children;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackableInterface;
import online.findfootball.android.firebase.database.FBDatabase;

/**
 * Created by WiskiW on 08.06.2017.
 *
 * Объект, с возможностью само сохранения, слушанья и удаления в Firebase Database
 * Позволяет обойтись без использования DatabaseLoader для изменения получения объекта из Firebase БД,
 * путем наличия методов save(), load(..) и listen(..)
 */

public abstract class PackableObject extends DatabaseLoader implements DatabasePackableInterface {

    private String dirPath;

    @Override
    public void setDirectoryPath(String directoryPath) {
        dirPath = directoryPath;
    }

    @Override
    public String getDirectoryPath() {
        return dirPath;
    }

    @Override
    public boolean hasLoaded() {
        return false;
    }

    public DataInstanceResult save() {
        return super.save(this);
    }

    public void load(OnLoadListener onLoadListener) {
        super.load(this, onLoadListener);
    }

    public void listen(OnListenListener onListenListener) {
        super.listen(this, onListenListener);
    }

    public void delete() {
        FBDatabase.getDatabaseReference(this).removeValue();
    }
}
