package online.findfootball.android.firebase.database.children;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.firebase.database.DatabaseSelfPackable;

/**
 * Created by WiskiW on 08.06.2017.
 * <p>
 * ArrayList реализующий DatabaseSelfPackable с возможностью сохранения,
 * слушанья и удаления в Firebase Database
 */

public class SelfPackableArrayList<T extends DatabasePackable> extends ArrayList<T>
        implements DatabaseSelfPackable, Parcelable {

    public static final SelfPackableArrayList<DatabasePackable> EMPTY =
            new SelfPackableArrayList<>();
    private String dirPath = "";


    public SelfPackableArrayList() {
        super();
    }

    @Override
    public void setDirectoryPath(String directoryPath) {
        dirPath = directoryPath;
    }

    @Override
    public String getDirectoryPath() {
        return dirPath;
    }

    @Override
    public boolean hasUnpacked() {
        return this.size() > 0;
    }

    @NonNull
    @Override
    public DataInstanceResult pack(@NonNull HashMap<String, Object> databaseMap) {
        if (size() > 0) {
            databaseMap.clear();
            for (T p : this) {
                packItem(databaseMap, p);
            }
        }
        return DataInstanceResult.onSuccess();
    }

    protected T unpackItem(DataSnapshot dataSnapshot) {
        return null;
    }

    protected void packItem(@NonNull HashMap<String, Object> databaseMap, T item) {
        if (item instanceof DatabaseSelfPackable) {
            HashMap<String, Object> itemMap = new HashMap<>();
            item.pack(itemMap);
            databaseMap.put(((DatabaseSelfPackable) item).getDirectoryPath(), itemMap);
        } else {
            throw new NoSuchElementException
                    (" you must override packItem() to pack the DatabasePackable list");
        }
    }

    @NonNull
    @Override
    public DataInstanceResult unpack(@NonNull DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.hasChildren()) {
                clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    T t = unpackItem(childSnapshot);
                    if (t == null) {
                        throw new NoSuchElementException
                                (" you must override unpackItem() to unpack the list");
                    }
                    add(t);
                }
            }
            return DataInstanceResult.onSuccess();
        } catch (Exception ex) {
            return new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE);
        }
    }

    @Override
    public DatabaseSelfPackable has(@NonNull DatabaseSelfPackable packable) {
        DatabaseSelfPackable result;
        for (DatabasePackable itemPackable : this) {
            if (itemPackable instanceof DatabaseSelfPackable) {
                result = ((DatabaseSelfPackable) itemPackable).has(packable);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dirPath);

        if (super.size() > 0) {
            Object item = super.get(0);
            if (item instanceof Parcelable) {
                dest.writeInt(super.size());
                final Class<?> objectsType = item.getClass();
                dest.writeSerializable(objectsType);
                dest.writeList(this);
            } else {
                dest.writeInt(0);
            }
        } else {
            dest.writeInt(0);
        }
    }

    private SelfPackableArrayList(Parcel in) {
        dirPath = in.readString();

        int size = in.readInt();
        if (size > 0) {
            Class<?> type = (Class<?>) in.readSerializable();
            in.readList(this, type.getClassLoader());
        }
    }

    public static final Parcelable.Creator<SelfPackableArrayList> CREATOR =
            new Parcelable.Creator<SelfPackableArrayList>() {
                public SelfPackableArrayList createFromParcel(Parcel in) {
                    return new SelfPackableArrayList(in);
                }

                public SelfPackableArrayList[] newArray(int size) {
                    return new SelfPackableArrayList[size];
                }
            };

}
