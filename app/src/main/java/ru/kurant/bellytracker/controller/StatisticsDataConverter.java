package ru.kurant.bellytracker.controller;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ru.kurant.bellytracker.controller.share.StringPatterns;
import ru.kurant.bellytracker.model.StatisticsCodec;
import ru.kurant.bellytracker.ui.StatisticsFragment;

public class StatisticsDataConverter {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private final Context context;

    public StatisticsDataConverter(Context context) {
        this.context = context;

    }

    private int[] getRandomColors(int arrSize) {
        int[] colors = new int[arrSize];
        Random rnd = new Random();
        for (int i = 0; i < colors.length; i++) {
            colors[i] = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        }
        return colors;
    }

    public static String calToString(Calendar cal) {
        SimpleDateFormat sdf = new SimpleDateFormat(StringPatterns.DB_DATE_PATTERN, Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    public ArrayList<String> getDateArray(Calendar dateFrom, Calendar dateTo) {
        ArrayList<String> dateArray = new ArrayList<>();
        for (; dateFrom.compareTo(dateTo) <= 0; dateFrom.add(Calendar.DAY_OF_YEAR, 1)) {
            dateArray.add(calToString(dateFrom));
        }
        return dateArray;
    }

    public View showStatistics(Calendar calFrom, Calendar calTo, StatisticsFragment.CurrentChartType chartType, boolean alcFlag) {
        switch (chartType) {
            case DAY_CHART:
                StatisticsCodec codec = new StatisticsCodec(context, calToString(calFrom), calToString(calTo));
                return prepareDayChart(codec, alcFlag);
            case PERIOD_BAR_CHART:
                return preparePeriodChart(calFrom, calTo, alcFlag);
            case PERIOD_PIE_CHART:
                codec = new StatisticsCodec(context, calToString(calFrom), calToString(calTo));
                return preparePieChart(codec, alcFlag);
            default:
                return null;
        }
    }

    private View preparePieChart(StatisticsCodec codec, boolean alcFlag) {
        List<PieEntry> pieEntries = new ArrayList<>();
        List<String> names = codec.getBeverageNames();
        List<Float> volumes = codec.getBeverageVolumes();
        int n;
        if (!alcFlag) {
            n = names.size();
            for (int i = 0; i < names.size(); i++) {
                pieEntries.add(new PieEntry(volumes.get(i), names.get(i)));
            }
        } else {
            n = 2;
            pieEntries.add(new PieEntry(codec.getAlcoholicVolume(), "Alcohol Drinks"));
            pieEntries.add(new PieEntry(codec.getNonAlcoholicVolume(), "Non-Alcohol Drinks"));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "PieDataSet Label");
        pieDataSet.setColors(getRandomColors(n));
        PieChart pieChart = new PieChart(context);
        pieChart.setData(new PieData(pieDataSet));
        return pieChart;
    }

    private View prepareDayChart(StatisticsCodec codec, boolean alcFlag) {
        List<IBarDataSet> barDataSets = new ArrayList<>();
        String[] names = codec.getNames();
        if (!alcFlag) {
            List<String> beverageNames = codec.getBeverageNames();
            for (String beverage : beverageNames) {
                List<BarEntry> barEntries = new ArrayList<>();
                for (int i = 0; i < names.length; i++) {
                    if (beverage.equals(names[i])) {
                        barEntries.add(new BarEntry(codec.getMoments()[i], codec.getVolumes()[i]));
                    }
                }
                BarDataSet barDataSet = new BarDataSet(barEntries, beverage);
                barDataSet.setColor(getRandomColors(1)[0]);
                barDataSets.add(barDataSet);
            }
        } else {
            List<BarEntry> alcBarEntries = new ArrayList<>();
            List<BarEntry> nonAlcBarEntries = new ArrayList<>();
            for (int i = 0; i < names.length; i++) {
                if (codec.getAlcFlags()[i]) {
                    alcBarEntries.add(new BarEntry(codec.getMoments()[i], codec.getVolumes()[i]));
                } else {
                    nonAlcBarEntries.add(new BarEntry(codec.getMoments()[i], codec.getVolumes()[i]));
                }
            }
            BarDataSet alcBarDataSet = new BarDataSet(alcBarEntries, "Alcohol Drinks");
            BarDataSet nonAlcBarDataSet = new BarDataSet(alcBarEntries, "Non-Alcohol Drinks");
            int[] colors = getRandomColors(2);
            alcBarDataSet.setColor(colors[0]);
            nonAlcBarDataSet.setColor(colors[1]);
            barDataSets.add(alcBarDataSet);
            barDataSets.add(nonAlcBarDataSet);
        }
        BarChart barChart = new BarChart(context);
        barChart.setData(new BarData(barDataSets));
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getLegend().setWordWrapEnabled(true);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setAxisMaximum(24);
        barChart.getXAxis().setAxisMinimum(0);
//        barChart.getXAxis().setValueFormatter(new MyXAxisValueFormatterMinutes());
        return barChart;
    }


    private View preparePeriodChart(Calendar calFrom, Calendar calTo, boolean alcFlag) {
        StatisticsCodec codec = new StatisticsCodec(context, calToString(calFrom), calToString(calTo));
        ArrayList<String> dates = getDateArray(calFrom, calTo);
        StatisticsCodec[] codecs = new StatisticsCodec[dates.size()];
        for (int i = 0; i < codecs.length; i++) {
            codecs[i] = new StatisticsCodec(context, dates.get(i));
        }

        List<String> names = codec.getBeverageNames();
        List<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < codecs.length; i++) {
            float x = codec.extractDays(dates.get(i));
            float[] yVals;
            if (!alcFlag) {
                yVals = new float[names.size()];
                List<String> dailyBeverageNames = codecs[i].getBeverageNames();
                List<Float> dailyBeveragevolumes = codecs[i].getBeverageVolumes();
                for (int k = 0; k < dailyBeverageNames.size(); k++) {
                    yVals[k] = dailyBeveragevolumes.get(k);
                }
            } else {
                yVals = new float[]{codecs[i].getNonAlcoholicVolume(), codecs[i].getAlcoholicVolume()};
            }
            barEntries.add(new BarEntry(x, yVals));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "BarDataSet Label");
        if (!alcFlag) {
            barDataSet.setColors(getRandomColors(names.size()));
            barDataSet.setStackLabels(names.toArray(new String[0]));
        } else {
            barDataSet.setColors(getRandomColors(2));
            barDataSet.setStackLabels(new String[]{"Non-Alcohol Drinks", "Alcohol Drinks"});
        }
        barDataSet.setDrawIcons(false);
        BarData barData = new BarData(barDataSet);
        BarChart barChart = new BarChart(context);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getLegend().setWordWrapEnabled(true);
        barChart.getDescription().setEnabled(false);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new MyXAxisValueFormatterDays());
        return barChart;
    }

    public static class MyXAxisValueFormatterDays extends IndexAxisValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            return dateFormat.format(new Date(TimeUnit.DAYS.toMillis((long) value)));
        }
    }

    public static class MyXAxisValueFormatterMinutes extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            int hour = Math.round(value / 360);
            return Integer.toString(hour);
        }
    }
}