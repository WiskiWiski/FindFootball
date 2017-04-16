package org.blackstork.findfootball.time;

import java.util.TimeZone;

/**
 * Created by WiskiW on 07.04.2017.
 */

public class TimeProvider {

    public static long getUtcTime() {
        return System.currentTimeMillis();
    }

    public static long convertToLocal(long utcMs) {
        return utcMs + TimeZone.getDefault().getOffset(utcMs);
    }


}
