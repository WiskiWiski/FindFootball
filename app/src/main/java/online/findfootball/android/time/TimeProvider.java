package online.findfootball.android.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by WiskiW on 07.04.2017.
 */

public class TimeProvider {

    public static final String FORMAT_LONG = "HH:mm dd.MM.yyyy";
    public static final String FORMAT_SHORT = "H:mm dd.MM";

    public static final String FORMAT_TIME = "HH:mm";
    public static final String FORMAT_DAY_1 = "dd.MM.yyyy";
    public static final String FORMAT_DAY_2 = "dd.MM E";
    public static final String FORMAT_DAY_3 = "dd.MM";

    public static long getUtcTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        return cal.getTimeInMillis();
    }

    public static long convertToLocal(long utcMs) {
        return utcMs + TimeZone.getDefault().getOffset(utcMs);
    }

    public static String getStringDate(String format, long time){
        long localDate = TimeProvider.convertToLocal(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(localDate);
    }


}
