package com.awesome.byunghwa.app.popularmoviesapp2.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ByungHwa on 7/15/2015.
 */
public class DateUtil {

    private static final String TAG = "DateUtil";
    private static String formattedDate;

    public static String formatDate(String dateString) {

        LogUtil.log_i(TAG, "Passed-in DateString: " + dateString);
        if (dateString != null) {
            String inputPattern = "yyyy-MM-dd";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);

            Date date = null;

            try {
                date = inputFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date != null) {
                formattedDate = DateFormat.getDateInstance().format(date);
                return formattedDate;
            }
        }

        return "";
    }
}
