package online.findfootball.android.firebase.database

import com.google.firebase.database.DataSnapshot
import java.util.*

/**
 * Created by WiskiW on 29.06.2017.
 */
interface DatabasePackable {

    // Упаковывает свои данные в DatabaseReference (на деле он же и сохраняет их)
    fun pack(databaseMap: HashMap<String, Any>): DataInstanceResult

    // Распаковывает данные в поля обекта через DataSnapshot
    fun unpack(dataSnapshot: DataSnapshot): DataInstanceResult

    fun hasUnpacked(): Boolean

    fun getPackablePath(): String?

    fun getPackableKey(): String?

    // Функция проверки наличия DatabasePackable внутри текущего DatabasePackable объекта.
    // При реализации обязана вернуть найденый дочерний DatabasePackable объекта;
    // если дочерних DatabasePackable нету, то null
    fun has(packable: DatabasePackable): DatabasePackable?

}