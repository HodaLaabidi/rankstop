package rankstop.steeringit.com.rankstop.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RSDateParser {

    public static String convertToDateTimeFormat(String date) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        input.setTimeZone(TimeZone.getTimeZone("UTC"));
        output.setTimeZone(TimeZone.getDefault());

        Date d = null;
        try {
            d = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d);
    }

    public static String convertToDateFormat(String date) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        input.setTimeZone(TimeZone.getTimeZone("UTC"));
        output.setTimeZone(TimeZone.getDefault());

        Date d = null;
        try {
            d = input.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return output.format(d);
    }
}
