package com.anshulvyas.csc780.grocerymanagr;

import java.util.Calendar;

/**
 * Created by av7 on 12/2/15.
 */
public class Util {

    public static int getDays(Calendar now) {
        long day = now.getTimeInMillis();
        int date = now.get(Calendar.DATE);
        return date;
    }
}
