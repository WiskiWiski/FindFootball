package online.findfootball.android.firebase.database.children;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabaseLoader;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.firebase.database.FBDatabase;

/**
 * Created by WiskiW on 08.06.2017.
 * <p>
 * ArrayList реализующий DatabaseSelfPackable с возможностью сохранения,
 * слушанья и удаления в Firebase Database
 */

public abstract class PackableArrayList<T extends DatabasePackable> extends DatabaseLoader
        implements DatabasePackable, Parcelable, List<T> {

    private String path = "";
    private String key = "";
    private ArrayList<T> list;


    public PackableArrayList() {
        list = new ArrayList<>();
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

    @Override
    public boolean hasUnpacked() {
        return this.size() > 0;
    }

    public DataInstanceResult save() {
        return super.save(this);
    }

    @NonNull
    @Override
    public DataInstanceResult pack(@NonNull HashMap<String, Object> databaseMap) {
        if (size() > 0) {
            for (T item : this) {
                HashMap<String, Object> itemMap = new HashMap<>();
                packItem(item, itemMap);
                databaseMap.put(item.getPackableKey(), itemMap);
            }
        }
        return DataInstanceResult.onSuccess();
    }

    protected DataInstanceResult packItem(@NonNull T item, @NonNull HashMap<String, Object> itemMap) {
        return item.pack(itemMap);
    }

    protected abstract T newItem(DataSnapshot itemSnapshot);

    @NonNull
    @Override
    public DataInstanceResult unpack(@NonNull DataSnapshot dataSnapshot) {
        try {
            if (dataSnapshot.hasChildren()) {
                list.clear();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    T t = newItem(itemSnapshot);
                    if (t == null) {
                        throw new NoSuchElementException
                                ("you must override newItem() to unpack the list");
                    }

                    String itemKey = t.getPackableKey();
                    if (itemKey == null || itemKey.isEmpty()) {
                        throw new NoSuchElementException
                                ("item key cannot be null(empty) to unpack the list");
                    }
                    if (unpackItem(t, itemSnapshot)) {
                        list.add(t);
                    }
                }
            }
            return DataInstanceResult.onSuccess();
        } catch (Exception ex) {
            Log.d("FFB:", "unpack: ", ex);
            return new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE, ex);
        }
    }

    protected boolean unpackItem(@NonNull T item, @NonNull DataSnapshot dataSnapshot) {
        item.unpack(dataSnapshot);
        return true;
    }

    @Override
    public DatabasePackable has(@NonNull DatabasePackable packable) {
        DatabasePackable result;
        for (DatabasePackable itemPackable : this) {
            result = itemPackable.has(packable);
            if (result != null) {
                return result;
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
        dest.writeString(path);
        dest.writeString(key);

        if (list.size() == 0) {
            dest.writeInt(0);
        } else {
            dest.writeInt(size());
            final Class<?> objectsType = list.get(0).getClass();
            dest.writeSerializable(objectsType);
            dest.writeList(this.list);
        }
    }

    protected PackableArrayList(Parcel in) {
        path = in.readString();
        key = in.readString();

        int size = in.readInt();
        this.list = new ArrayList<>(size);
        if (size > 0) {
            Class<?> objectsType = (Class<?>) in.readSerializable();
            in.readList(this.list, objectsType.getClassLoader());
        }
    }

    @Override
    public String toString() {
        return list.toString();
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

    // LIST INTERFACE bellow

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return list.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        list.add(index, element);
    }

    @Override
    public T remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
}
