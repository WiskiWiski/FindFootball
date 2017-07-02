package online.findfootball.android.game.football.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.children.PackableArrayList;
import online.findfootball.android.game.GameObj;

/**
 * Created by WiskiW on 02.07.2017.
 */

public class FootballGameList extends PackableArrayList<GameObj> {

    private static final String FOOTBALL_LIST_KEY = "/events/football/";

    public FootballGameList() {

    }

    private FootballGameList(Parcel in) {
        super(in);
    }

    @Override
    protected GameObj newItem(DataSnapshot itemSnapshot) {
        return new GameObj(itemSnapshot.getKey());
    }

    @Override
    protected DataInstanceResult packItem(@NonNull GameObj item,
                                          @NonNull HashMap<String, Object> itemMap) {
        String eid = item.getEid();
        itemMap.put(eid, eid);
        return DataInstanceResult.onSuccess();
    }

    @NonNull
    @Override
    public String getPackableKey() {
        return FOOTBALL_LIST_KEY;
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<FootballGameList> CREATOR =
            new Parcelable.Creator<FootballGameList>() {
                public FootballGameList createFromParcel(Parcel in) {
                    return new FootballGameList(in);
                }

                public FootballGameList[] newArray(int size) {
                    return new FootballGameList[size];
                }
            };

}
