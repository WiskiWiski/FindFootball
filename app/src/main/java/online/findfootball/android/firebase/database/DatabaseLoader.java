package online.findfootball.android.firebase.database;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import online.findfootball.android.firebase.database.children.PackableArrayList;

/**
 * Created by WiskiW on 06.06.2017.
 */

public class DatabaseLoader {


    private ValueEventListener singleValueListener;
    private ChildEventListener childEventListener;
    private boolean inLoading;
    private DatabasePackableInterface packable;

    public DatabaseLoader() {

    }

    public static DatabaseLoader newLoader() {
        return new DatabaseLoader();
    }

    private ValueEventListener createSingleValueListener(final OnLoadListener onLoadListener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    if (onLoadListener != null)
                        onLoadListener.onComplete(new DataInstanceResult(DataInstanceResult.CODE_NULL_SNAPSHOT,
                                DataInstanceResult.MSG_NULL_SNAPSHOT), packable);
                    return;
                }
                if (!dataSnapshot.hasChildren()) {
                    if (onLoadListener != null)
                        onLoadListener.onComplete(new DataInstanceResult(DataInstanceResult.CODE_HAS_REMOVED),
                                packable);
                    return;

                }
                if (onLoadListener != null)
                    onLoadListener.onComplete(packable.unpack(dataSnapshot), packable);
                abortAllLoadings();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onLoadListener != null)
                    onLoadListener.onComplete(new DataInstanceResult(DataInstanceResult.CODE_LOADING_FAILED,
                            databaseError.getMessage(),
                            databaseError.toException()), packable);
                abortAllLoadings();
            }
        };
    }

    private ChildEventListener createChildEventListener(final OnListenListener onListenListener) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Log.d("FFB:DatabaseLoader", "onChildAdded: ");
                onListenListener.onChildAdded(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //Log.d("FFB:DatabaseLoader", "onChildChanged: ");
                onListenListener.onChildChanged(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Log.d("FFB:DatabaseLoader", "onChildRemoved: ");
                onListenListener.onChildRemoved(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Log.d("FFB:DatabaseLoader", "onChildMoved: ");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.d("FFB:DatabaseLoader", "onCancelled: ");
            }
        };
    }

    public DataInstanceResult save(DatabasePackableInterface packable) {
        this.packable = packable;
        return packable.pack(FBDatabase.getDatabaseReference(packable));
    }

    // Устанавливает одноразового слушателя обновлений на текуший экземпляр
    public void load(DatabasePackableInterface packable, OnLoadListener onLoadListener) {
        abortAllLoadings();
        this.packable = packable;
        singleValueListener = createSingleValueListener(onLoadListener);
        FBDatabase.getDatabaseReference(packable).addListenerForSingleValueEvent(singleValueListener);
    }

    public void loadLast(PackableArrayList packableList, int count, OnLoadListener onLoadListener) {
        abortAllLoadings();
        this.packable = packableList;
        Query myTopPostsQuery = FBDatabase.getDatabaseReference(packable).limitToLast(count);
        singleValueListener = createSingleValueListener(onLoadListener);
        myTopPostsQuery.addListenerForSingleValueEvent(singleValueListener);
    }

    // Устанавливает слушателя обновлений на текуший экземпляр
    public void listen(DatabasePackableInterface packable, final OnListenListener onListenListener) {
        abortAllLoadings();
        this.packable = packable;
        childEventListener = createChildEventListener(onListenListener);
        FBDatabase.getDatabaseReference(packable).addChildEventListener(childEventListener);
    }

    // Прерывает все текущие обновления, загрузки, слушанья и т.д. для данного экземпляра
    public void abortAllLoadings() {
        if (packable != null && (singleValueListener != null || childEventListener != null)) {
            DatabaseReference r = FBDatabase.getDatabaseReference(packable);
            if (singleValueListener != null) {
                r.removeEventListener(singleValueListener);
                singleValueListener = null;
            }
            if (childEventListener != null) {
                r.removeEventListener(childEventListener);
                childEventListener = null;
            }
        }
        inLoading = false;
    }

    public boolean isLoading() {
        return inLoading;
    }

    public interface OnLoadListener {

        void onComplete(DataInstanceResult result, DatabasePackableInterface packable);

    }

    public interface OnListenListener {

        void onChildAdded(DataSnapshot dataSnapshot);

        void onChildChanged(DataSnapshot dataSnapshot);

        void onChildRemoved(DataSnapshot dataSnapshot);

    }

}
