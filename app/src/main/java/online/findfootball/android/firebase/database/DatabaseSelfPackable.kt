package online.findfootball.android.firebase.database

/**
 * Created by WiskiW on 29.06.2017.
 */
interface DatabaseSelfPackable : DatabasePackable {

    fun setDirectoryPath(directoryPath: String)

    fun getDirectoryPath(): String

    // Функция проверки наличия DatabasePackable внутри текущего DatabasePackable объекта.
    // При реализации обязана вернуть найденый дочерний DatabasePackable объекта;
    // если дочерних DatabasePackable нету, то null
    fun has(selfPackable: DatabaseSelfPackable): DatabaseSelfPackable?

}