package online.findfootball.android.firebase.database.children;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabasePackableInterface;

/**
 * Created by WiskiW on 08.06.2017.
 *
 * ArrayList реализующий DatabasePackableInterface с возможностью сохранения,
 * слушанья и удаления в Firebase Database
 */

public class PackableArrayList<T extends DatabasePackableInterface> extends ArrayList<T>
        implements DatabasePackableInterface, Parcelable, Serializable {

    private String dirPath = "";

    public PackableArrayList() {
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
    public boolean hasLoaded() {
        return false;
    }

    @Override
    public DataInstanceResult pack(DatabaseReference databaseReference) {
        databaseReference.removeValue();
        if (size() > 0) {
            for (T p : this) {
                packItem(databaseReference, p);
            }
        }
        return DataInstanceResult.onSuccess();
    }

    protected T unpackItem(DataSnapshot dataSnapshot) {
        return null;
    }

    protected void packItem(DatabaseReference databaseReference, T item) {
        item.pack(databaseReference.child(item.getDirectoryPath()));
    }


    @Override
    public DataInstanceResult unpack(DataSnapshot dataSnapshot) {
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dirPath);
    }

    protected PackableArrayList(Parcel in) {
        dirPath = in.readString();
    }

    public static final Parcelable.Creator<PackableArrayList> CREATOR = new Parcelable.Creator<PackableArrayList>() {
        public PackableArrayList createFromParcel(Parcel in) {
            return new PackableArrayList(in);
        }

        public PackableArrayList[] newArray(int size) {
            return new PackableArrayList[size];
        }
    };

}
