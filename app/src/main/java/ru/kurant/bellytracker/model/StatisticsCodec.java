package ru.kurant.bellytracker.model;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ru.kurant.bellytracker.controller.share.StringPatterns;
import ru.kurant.bellytracker.model.room.DrinkDao;
import ru.kurant.bellytracker.model.room.DrinkTypeDao;
import ru.kurant.bellytracker.model.room.SingleDBService;

/*  Class for storing db requests results in statistics-friendly format
*   Обработчик данных для статистики */
public class StatisticsCodec {

    private final DrinkTypeDao drinkTypeDao;
    private final DrinkDao drinkDao;

    /*  Сколько надо потоков? 0_0 Let it be four */
    private final ExecutorService dbThread = Executors.newFixedThreadPool(4);

    /*  Данные обо всем списке событий
    *   Full statistics of drinks*/
    private String[] names;
    private float[] volumes;
    private boolean[] alcFlags;
    private float[] moments;

    /*  Статистика по типам напитков
    *   Beverage-based statistics */
    private List<String> beverageNames;
    private List<Float> beverageVolumes;
    private List<Boolean> beverageAlcFlags;

    /*  Статистика по алкоголю
    *   Alcohol-based statistics */
    private float alcoholicVolume;
    private float nonAlcoholicVolume;
    private float generalVolume;
    private float alcDose;

    public StatisticsCodec(Context context, String date) {
        this(context, date, date);
    }

    public StatisticsCodec(Context context, String dateFrom, String dateTo) {
        SingleDBService dbService = SingleDBService.getInstance(context);
        drinkTypeDao = dbService.getDrinkTypeDao();
        drinkDao = dbService.getDrinkDao();
        extractArrays(getDrinksByDate(dateFrom, dateTo));
    }

    private List<Drink> getDrinksByDate(String time1, String time2) {
        List<Drink> drinks = new ArrayList<>();
        Future<List<Drink>> future = dbThread.submit(new Callable<List<Drink>>() {
            @Override
            public List<Drink> call() {
                if (! time1.equals(time2)) {
                    return drinkDao.getDrinkByDate(time1 + "%", time2 + "%");
                } else {
                    return drinkDao.getDrinkByDate(time2 + "%");
                }
            }
        });
        try {
            drinks = future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return drinks;
        }
        return drinks;
    }

    private void extractArrays(List<Drink> drinks) {

        int n = drinks.size();
        names = new String[n];
        volumes = new float[n];
        alcFlags = new boolean[n];
        moments = new float[n];

        alcDose = 0f;
        alcoholicVolume = 0f;
        nonAlcoholicVolume = 0f;
        generalVolume = 0f;

        beverageNames = new ArrayList<>();
        beverageAlcFlags = new ArrayList<>();
        beverageVolumes = new ArrayList<>();

        for (Drink d : drinks) {
            int index = drinks.indexOf(d);
            DrinkType beverage = getDrinkType(d);

            String name = beverage.getDrinkName();
            boolean alcFlag = beverage.getAlcoholPercent() > StringPatterns.alcoholLevelTrigger;
            float volume = (float) d.getVolume();
            float pureAlc = beverage.getAlcoholPercent() * volume / 100;

            names[index] = name;
            alcFlags[index] = alcFlag;
            volumes[index] = volume;
            moments[index] = extractTime(d.getDrinkTime());

            int beverageNumber;
            if (beverageNames.contains(name)) {
                beverageNumber = beverageNames.indexOf(name);
                beverageVolumes.set(beverageNumber, beverageVolumes.get(beverageNumber) + volume);
            } else {
                beverageNames.add(name);
                beverageNumber = beverageNames.indexOf(name);
                beverageVolumes.add(beverageNumber, volume);
                beverageAlcFlags.add(beverageNumber, alcFlag);
            }
            if (alcFlag) {
                alcoholicVolume += volume;
            } else {
                nonAlcoholicVolume += volume;
            }
            alcDose += pureAlc;
            generalVolume += volume;
        }
    }

    public float extractTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(StringPatterns.DB_DATE_TIME_PATTERN, Locale.getDefault());
        SimpleDateFormat sdfDate = new SimpleDateFormat(StringPatterns.DB_DATE_PATTERN, Locale.getDefault());
        Date dateValue;
        Date date0;
        try {
            dateValue = sdf.parse(time);
            date0 = sdfDate.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0f;
        }
        return (float) ((dateValue.getTime() - date0.getTime()) / 3600000);
    }

    public float extractDays(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(StringPatterns.DB_DATE_PATTERN, Locale.getDefault());
        Date dateNative;
        try {
            dateNative = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0f;
        }
        return (float) TimeUnit.MILLISECONDS.toDays(dateNative.getTime());
    }

    private DrinkType getDrinkType(Drink d) {
        Future<DrinkType> future = dbThread.submit(new Callable<DrinkType>() {
            @Override
            public DrinkType call() throws Exception {
                return drinkTypeDao.selectDrinkTypeById(d.getDrinkType());
            }
        });
        DrinkType beverageType;
        try {
            beverageType = future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return beverageType;
    }

    public String[] getNames() {
        return names;
    }

    public float[] getVolumes() {
        return volumes;
    }

    public boolean[] getAlcFlags() {
        return alcFlags;
    }

    public float[] getMoments() {
        return moments;
    }

    public List<String> getBeverageNames() {
        return beverageNames;
    }

    public List<Float> getBeverageVolumes() {
        return beverageVolumes;
    }

    public List<Boolean> getBeverageAlcFlags() {
        return beverageAlcFlags;
    }

    public float getAlcoholicVolume() {
        return alcoholicVolume;
    }

    public float getNonAlcoholicVolume() {
        return nonAlcoholicVolume;
    }

    public float getGeneralVolume() {
        return generalVolume;
    }

    public float getAlcDose() {
        return alcDose;
    }
}
