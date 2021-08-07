package ru.kurant.bellytracker.controller.share;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*  Класс для сохранения глобальных переменных
*   Keeper for global level variables*/
public class StringPatterns {

    public static final String DB_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DB_DATE_PATTERN = "yyyy-MM-dd";
    public static String datePattern;
    public static String timePattern;

    public static double alcoholLevelTrigger;

    private StringPatterns() {
    }

/*  This method converts time value (String) from db into custom format
*   Конвертирует время из базы данных в пользовательский формат */
    public static String convertTimeForUser (String inString) {
        Date dateTime = new Date();
        SimpleDateFormat sdfIn = new SimpleDateFormat(DB_DATE_TIME_PATTERN, Locale.getDefault());
        SimpleDateFormat sdfOut = new SimpleDateFormat(datePattern + " " + timePattern, Locale.getDefault());
        try {
            dateTime = sdfIn.parse(inString);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        return sdfOut.format(dateTime);
    }

/*  This method converts String from custom datetime format into db format
*   Конвертирует время из пользовательского формата в формат базы данных */
    public static String convertTimeForDB (String inString) {
        String outString = "";

        SimpleDateFormat sdfIn = new SimpleDateFormat(DB_DATE_TIME_PATTERN, Locale.getDefault());
        SimpleDateFormat sdfOut = new SimpleDateFormat(datePattern + " " + timePattern, Locale.getDefault());
        try {
            Date dateTime = sdfOut.parse(inString);
            outString = sdfIn.format(dateTime);
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
        }
        return outString;
    }
}
