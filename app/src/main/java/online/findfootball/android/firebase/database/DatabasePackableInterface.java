package online.findfootball.android.firebase.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by WiskiW on 06.06.2017.
 */

public interface DatabasePackableInterface {

    void setDirectoryPath(String directoryPath);

    String getDirectoryPath();

    boolean hasLoaded();

    // Упаковывает свои данные в DatabaseReference (на деле он же и сохраняет их)
    DataInstanceResult pack(DatabaseReference databaseReference);

    // Распаковывает данные в поля обекта через DataSnapshot
    DataInstanceResult unpack(DataSnapshot dataSnapshot);

    // Функция проверки наличия DatabasePackable внутри текущего DatabasePackable объекта.
    // При реализации обязана вернуть найденые дочерние DatabasePackable объекта;
    // если дочерних DatabasePackable нету, то null
    DatabasePackableInterface has(DatabasePackableInterface packable);

}
