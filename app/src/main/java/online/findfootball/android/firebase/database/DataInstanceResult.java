package online.findfootball.android.firebase.database;

/**
 * Created by WiskiW on 02.06.2017.
 *
 * Хранит информацию о ошибках при загрузке или слушанье DatabasePackable объекта
 */

public class DataInstanceResult {

    public static final int CODE_PARSING_FAILED = 2; // Парсинг провалился: дальнейшая работа невозможна (наивысший приоритет)
    public static final int CODE_NOT_COMPLETE = 1; // Парсинг с ошибками: дальнейшая работа возможна
    public static final int CODE_SUCCESS = 0; // Парсинг успешен (низший приоритет)

    public static final int CODE_NULL_SNAPSHOT = 5;
    public static final int CODE_HAS_REMOVED = 6;
    public static final int CODE_LOADING_ABORTED = 3;
    public static final int CODE_NOT_ENOUGH_DATA = 7;
    public static final int CODE_NO_PERMISSIONS = 8;
    public static final int CODE_NULL_INSTANCE = 9;

    public static final int CODE_LOADING_FAILED = 4;

    public static final String MSG_LOADING_ABORTED = "Loading has been aborted";
    public static final String MSG_NULL_SNAPSHOT = "Data snapshot is null";

    private int code;
    private String message;
    private Throwable cause;

    public DataInstanceResult() {
    }

    public DataInstanceResult(int code) {
        this.code = code;
    }

    public DataInstanceResult(int code, String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
        this.code = code;
    }

    public DataInstanceResult(int code, Throwable cause) {
        this.code = code;
        this.cause = cause;
    }

    public DataInstanceResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public DataInstanceResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public Throwable getCause() {
        return cause;
    }

    public boolean isFailed() {
        return code == CODE_PARSING_FAILED;
    }

    public boolean isNotCompete() {
        return code == CODE_NOT_COMPLETE;
    }

    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static DataInstanceResult onSuccess() {
        return new DataInstanceResult(CODE_SUCCESS);
    }

    public static DataInstanceResult onFailed(String msg, Throwable ex) {
        return new DataInstanceResult(CODE_PARSING_FAILED, msg, ex);
    }

    public static DataInstanceResult onFailed(String msg) {
        return onFailed(msg, null);
    }

    public static DataInstanceResult notComplete(String msg) {
        return notComplete(msg, null);
    }

    public static DataInstanceResult notComplete(String msg, Throwable ex) {
        return new DataInstanceResult(CODE_NOT_COMPLETE, msg, ex);
    }

    public static DataInstanceResult calculateResult(DataInstanceResult prevUr, DataInstanceResult newUr) {
        if (newUr != null) {
            if (newUr.isFailed()) {
                return newUr;
            } else if (prevUr != null) {
                if (prevUr.isFailed() || (!prevUr.isSuccess() && newUr.isSuccess())) {
                    return prevUr;
                }
            }
            return newUr;
        } else {
            return prevUr;
        }
    }

}
