package online.findfootball.android.user;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import online.findfootball.android.firebase.database.DataInstanceResult;
import online.findfootball.android.firebase.database.DatabasePackable;
import online.findfootball.android.firebase.database.children.PackableObject;
import online.findfootball.android.game.GameObj;
import online.findfootball.android.game.football.object.FootballGameList;

/**
 * Created by WiskiW on 17.04.2017.
 */

@SuppressWarnings("NullableProblems")
public class UserObj extends PackableObject {

    private final static String PATH_USERS = "users/";
    final static String PATH_GAMES_FOOTBALL = "/events/football/";

    public final static String EMPTY_UID = "empty_uid";

    public final static String PATH_DISPLAY_NAME = "display_name";
    public final static String PATH_EMAIL = "email";
    public final static String PATH_SEX = "sex";
    public final static String PATH_RATE = "rate";
    public final static String PATH_LEVEL = "level";
    public final static String PATH_AGE = "age";
    public final static String PATH_PHOTO_URL = "photo_url";
    public final static String PATH_REGISTER_TIME = "register_time";
    public final static String PATH_LAST_ACTIVITY_TIME = "last_activity_time";
    public final static String PATH_AUTH_PROVIDER = "auth_provider";
    public final static String PATH_CLOUD_MESSAGE_TOKEN = "cm_token";

    public final static UserObj EMPTY = new UserObj(EMPTY_UID);

    private String uid;
    private String displayName;
    private String email;
    private Sex sex;
    private float rate;
    private int level;
    private int age;
    private UserContacts contacts;
    private String cloudMessageToken;
    private long lastActivityTime;
    private long registerTime;
    private Uri photoUrl;
    private String authProvider;
    private FootballGameList gameList;

    protected UserObj() {
        this.uid = EMPTY_UID;
    }

    public UserObj(String uid) {
        this.uid = uid;
    }

    public UserObj(FirebaseUser firebaseUser) {
        this.initByFirebaseUser(firebaseUser);
    }

    public void initByFirebaseUser(FirebaseUser fUser) {
        setUid(fUser.getUid());
        setDisplayName(fUser.getDisplayName());
        setEmail(fUser.getEmail());
        setPhotoUrl(fUser.getPhotoUrl());

    }

    public boolean isEmpty() {
        return this.uid.equals(EMPTY_UID);
    }

    private void initGameList(FootballGameList list) {
        list.setPackablePath(this.getPackablePath() + "/" + this.getPackableKey());
    }

    public FootballGameList getGameList() {
        if (gameList == null) {
            gameList = new FootballGameList();
            initGameList(gameList);
        }
        return gameList;
    }

    public String getCloudMessageToken() {
        return this.cloudMessageToken;
    }

    public void setCloudMessageToken(String cloudMessageToken) {
        this.cloudMessageToken = cloudMessageToken;
    }

    public void setGameList(FootballGameList gameList) {
        initGameList(gameList);
        this.gameList = gameList;
    }

    public void addGame(GameObj gameObj) {
        if (gameList.contains(gameObj)) {
            gameList.remove(gameObj);
        }
        gameList.add(gameObj);
    }

    public void removeGame(GameObj gameObj) {
        if (gameList != null) {
            gameList.remove(gameObj);
        }
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public Uri getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(Uri photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public UserContacts getContacts() {
        if (this.contacts == null) {
            this.contacts = new UserContacts();
        }
        return contacts;
    }

    public void setContacts(UserContacts contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "UserId:" + getUid();
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(uid);
        out.writeString(displayName);
        out.writeString(email);
        out.writeString(cloudMessageToken);
        out.writeString(authProvider);
        out.writeSerializable(sex);
        out.writeFloat(rate);
        out.writeInt(level);
        out.writeInt(age);
        out.writeParcelable(contacts, flags);
        out.writeLong(lastActivityTime);
        out.writeLong(registerTime);
        out.writeParcelable(photoUrl, flags);
        out.writeParcelable(gameList, flags);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<UserObj> CREATOR = new Parcelable.Creator<UserObj>() {
        public UserObj createFromParcel(Parcel in) {
            return new UserObj(in);
        }

        public UserObj[] newArray(int size) {
            return new UserObj[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    public UserObj(Parcel in) {
        super(in);
        uid = in.readString();
        displayName = in.readString();
        email = in.readString();
        cloudMessageToken = in.readString();
        authProvider = in.readString();
        sex = (Sex) in.readSerializable();
        rate = in.readFloat();
        level = in.readInt();
        age = in.readInt();
        contacts = in.readParcelable(UserContacts.class.getClassLoader());
        lastActivityTime = in.readLong();
        registerTime = in.readLong();
        photoUrl = in.readParcelable(Uri.class.getClassLoader());
        in.readParcelable(GameObj.class.getClassLoader());
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        UserObj tmp = (UserObj) obj;
        return tmp.getUid().equals(this.getUid());
    }

    @Override
    public int hashCode() {
        return 31 * this.getUid().hashCode();
    }

    @Override
    public String getPackablePath() {
        return PATH_USERS;
    }

    @Override
    public void setPackableKey(String key) {
        super.setPackableKey(key);
        uid = key;
    }

    @Override
    public String getPackableKey() {
        return uid;
    }

    @Override
    public boolean hasUnpacked() {
        return photoUrl != null && email != null && displayName != null
                && getGameList().hasUnpacked();
    }

    @NonNull
    @Override
    public DataInstanceResult unpack(DataSnapshot dataSnapshot) {
        DataInstanceResult r = DataInstanceResult.onSuccess();
        try {
            setUid(dataSnapshot.getKey());
            setDisplayName((String) dataSnapshot.child(PATH_DISPLAY_NAME).getValue());
            String url = (String) dataSnapshot.child(PATH_PHOTO_URL).getValue();
            if (url != null) {
                setPhotoUrl(Uri.parse(url));
            } else {
                DataInstanceResult.calculateResult(r, new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE));
            }
            setEmail((String) dataSnapshot.child(PATH_EMAIL).getValue());
            setCloudMessageToken((String) dataSnapshot.child(PATH_CLOUD_MESSAGE_TOKEN).getValue());
            setAuthProvider((String) dataSnapshot.child(PATH_AUTH_PROVIDER).getValue());

            final Object regTimeObj = dataSnapshot.child(PATH_REGISTER_TIME).getValue();
            if (regTimeObj != null) {
                setRegisterTime((Long) regTimeObj);
            } else {
                DataInstanceResult.calculateResult(r, new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE));
            }

            final Object lastActObj = dataSnapshot.child(PATH_LAST_ACTIVITY_TIME).getValue();
            if (lastActObj != null) {
                setLastActivityTime((Long) lastActObj);
            } else {
                DataInstanceResult.calculateResult(r, new DataInstanceResult(DataInstanceResult.CODE_NOT_COMPLETE));
            }

            if (gameList == null) {
                gameList = new FootballGameList();
                initGameList(gameList);
            }
            gameList.unpack(dataSnapshot.child(PATH_GAMES_FOOTBALL));


            final Object rateObj = dataSnapshot.child(PATH_RATE).getValue();
            if (rateObj != null) {
                rate = ((Double) rateObj).floatValue();
            }

            final Object levelObj = dataSnapshot.child(PATH_LEVEL).getValue();
            if (levelObj != null) {
                level = ((Long) levelObj).intValue();
            }

            final Object ageObj = dataSnapshot.child(PATH_AGE).getValue();
            if (ageObj != null) {
                age = ((Long) ageObj).intValue();
            }

            final Object sexObj = dataSnapshot.child(PATH_SEX).getValue();
            if (sexObj != null) {
                sex = Sex.lookup(sexObj.toString());
            }

            getContacts().unpack(dataSnapshot);

            return r;
        } catch (Exception ex) {
            return DataInstanceResult.onFailed(ex.getMessage(), ex);
        }
    }

    @Override
    public DatabasePackable has(@NonNull DatabasePackable packable) {
        return getGameList().has(packable);
    }

    @NonNull
    @Override
    public DataInstanceResult pack(@NonNull HashMap<String, Object> databaseMap) {
        if (this.getClass() == UserObj.class) {
            // т к у простого юзер-объекта нет права на сохранение
            return new DataInstanceResult(DataInstanceResult.CODE_NO_PERMISSIONS);
        } else {
            databaseMap.put(PATH_DISPLAY_NAME, getDisplayName());
            databaseMap.put(PATH_PHOTO_URL, getPhotoUrl().toString());
            databaseMap.put(PATH_EMAIL, getEmail());
            databaseMap.put(PATH_SEX, getSex());
            databaseMap.put(PATH_RATE, getRate());
            databaseMap.put(PATH_LEVEL, getLevel());
            databaseMap.put(PATH_AGE, getAge());
            databaseMap.put(PATH_CLOUD_MESSAGE_TOKEN, getCloudMessageToken());
            databaseMap.put(PATH_REGISTER_TIME, getRegisterTime());
            databaseMap.put(PATH_LAST_ACTIVITY_TIME, getLastActivityTime());
            databaseMap.put(PATH_AUTH_PROVIDER, getAuthProvider());

            HashMap<String, Object> gameListMap = new HashMap<>();
            getGameList().pack(gameListMap);
            databaseMap.put(PATH_GAMES_FOOTBALL, gameListMap);

            final UserContacts uContacts = getContacts();
            if (uContacts != null && !uContacts.isEmpty()) {
                HashMap<String, Object> userContactsMap = new HashMap<>();
                uContacts.pack(userContactsMap);
                databaseMap.put(uContacts.getPackableKey(), userContactsMap);
            }

            return DataInstanceResult.onSuccess();
        }
    }

    public enum Sex {

        MALE, FEMALE, OTHER;

        public static Sex lookup(String strSex) {
            try {
                return Sex.valueOf(strSex.toUpperCase());
            } catch (IllegalArgumentException e) {
                return OTHER;
            }
        }

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    public static class UserContacts extends PackableObject implements Parcelable {

        private final static String PATH_CONTACTS = "contacts";

        private HashMap<ContactKey, String> contacts;

        UserContacts() {
            contacts = new HashMap<>();
        }

        public UserContacts(Parcel source) {
            contacts = new HashMap<>();
            final int size = source.readInt();
            for (int i = 0; i < size; i++) {
                contacts.put((ContactKey) source.readSerializable(), source.readString());
            }
        }

        public UserContacts putContact(ContactKey key, String value) {
            this.contacts.put(key, value);
            return this;
        }

        public String getContact(ContactKey key) {
            return this.contacts.get(key);
        }

        public boolean isEmpty() {
            return contacts.size() <= 0;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            final int s = contacts.size();
            dest.writeInt(s);
            if (s > 0) {
                for (Map.Entry<ContactKey, String> entry : contacts.entrySet()) {
                    dest.writeSerializable(entry.getKey());
                    dest.writeString(entry.getValue());
                }
            }
        }

        public static final Creator<UserContacts> CREATOR = new Creator<UserContacts>() {
            public UserContacts createFromParcel(Parcel source) {
                return new UserContacts(source);
            }

            public UserContacts[] newArray(int size) {
                return new UserContacts[size];
            }
        };

        @NotNull
        @Override
        public DataInstanceResult pack(HashMap<String, Object> databaseMap) {
            if (contacts.size() > 0) {
                for (Map.Entry<ContactKey, String> entry : contacts.entrySet()) {
                    databaseMap.put(entry.getKey().toString(), entry.getValue());
                }
            }
            return DataInstanceResult.onSuccess();
        }

        @NotNull
        @Override
        public DataInstanceResult unpack(DataSnapshot dataSnapshot) {
            DataSnapshot contactsSnapshot = dataSnapshot.child(PATH_CONTACTS);
            if (contactsSnapshot.hasChildren()) {
                for (DataSnapshot contactSnapshot : contactsSnapshot.getChildren()) {
                    UserContacts.ContactKey key = UserContacts.ContactKey.lookup(contactSnapshot.getKey());
                    contacts.put(key, (String) contactSnapshot.getValue());
                }
            }
            return DataInstanceResult.onSuccess();
        }

        @Override
        public boolean hasUnpacked() {
            return contacts.size() > 0;
        }

        @Override
        public String getPackableKey() {
            return PATH_CONTACTS;
        }

        public enum ContactKey {

            VK, FB, INSTAGRAM, TEL, TELEGRAM,
            WHATS_APP, VIBER, NUN;

            public static ContactKey lookup(String strSex) {
                try {
                    return ContactKey.valueOf(strSex.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return NUN;
                }
            }

            @Override
            public String toString() {
                return super.toString().toLowerCase();
            }
        }

    }

}
