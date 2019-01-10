package rankstop.steeringit.com.rankstop.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String convertToTimeFormat(String date) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat("HH:mm", Locale.getDefault());
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

    public static String convertToDateFormat(String date, String format) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat(format, Locale.getDefault());
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
    public static String convertToDateFormat(String date, String inputFormat, String outputFormat) {
        SimpleDateFormat input = new SimpleDateFormat(inputFormat,Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat(outputFormat, Locale.getDefault());
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

    public static Calendar convertToDate(String date, String format){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
