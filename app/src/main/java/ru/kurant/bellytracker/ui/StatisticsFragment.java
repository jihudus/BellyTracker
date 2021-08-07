package ru.kurant.bellytracker.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.controller.StatisticsDataConverter;

public class StatisticsFragment extends Fragment {

    private Button dateFromButton;
    private Button dateToButton;
    private Button showButton;
    private SwitchCompat alcoholDrinksSwitch;

    private FrameLayout frame;

    private Calendar dateFrom;
    private Calendar dateTo;

    private CurrentChartType chartType = CurrentChartType.PERIOD_PIE_CHART;
    private boolean isAlcoholCalculatorOn = false;

    private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

    private StatisticsDataConverter statisticsDataConverter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        statisticsDataConverter = new StatisticsDataConverter(getContext());

        dateFrom = Calendar.getInstance(TimeZone.getDefault());
        dateFrom.add(Calendar.DAY_OF_YEAR, -1);
        dateTo = Calendar.getInstance(TimeZone.getDefault());
        dateTo.add(Calendar.DAY_OF_YEAR, -1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initButtons(view);
        initDateLabels();
        frame = view.findViewById(R.id.statistics_frame);
    }

    private void initDateLabels() {
        String dateFromString = dateFormat.format(dateFrom.getTime());
        String dateToString = dateFormat.format(dateTo.getTime());
        dateFromButton.setText(dateFromString);
        dateToButton.setText(dateToString);
    }

    private void initButtons(View view) {

        dateFromButton = view.findViewById(R.id.button_from);
        setFromButton();
        dateToButton = view.findViewById(R.id.button_to);
        showButton = view.findViewById(R.id.button_show);

        initSwitches(view);

        dateToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateTo.set(year, month, dayOfMonth);
                                dateToButton.setText(dateFormat.format(dateTo.getTime()));
                            }
                        },
                        dateTo.get(Calendar.YEAR), dateTo.get(Calendar.MONTH), dateTo.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View stats = statisticsDataConverter
                        .showStatistics(dateFrom, dateTo, chartType, isAlcoholCalculatorOn);
                attachStatisticsView(stats);
            }
        });
    }

    private void initSwitches(View view) {
        alcoholDrinksSwitch = view.findViewById(R.id.switch_alc_drinks);
        alcoholDrinksSwitch.setChecked(isAlcoholCalculatorOn);
        alcoholDrinksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAlcoholCalculatorOn = isChecked;
            }
        });
    }

    private void setFromButton() {
        if (chartType == CurrentChartType.DAY_CHART) {
            dateFromButton.setAlpha(0.2f);
            dateFromButton.setClickable(false);
        } else {
            dateFromButton.setText(R.string.from_date);
            dateFromButton.setAlpha(1f);
            dateFromButton.setClickable(true);
            dateFromButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    dateFrom.set(year, month, dayOfMonth);
                                    dateFromButton.setText(dateFormat.format(dateFrom.getTime()));
                                }
                            },
                            dateFrom.get(Calendar.YEAR), dateFrom.get(Calendar.MONTH), dateFrom.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });
        }
    }

    private void attachStatisticsView(View stats) {
        frame.removeAllViews();
        if (!dateTo.after(dateFrom)) {
            dateFrom.setTime(dateTo.getTime());
            chartType = CurrentChartType.DAY_CHART;
            initDateLabels();
        }
        frame.addView(stats);
        stats.invalidate();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu_statistics, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_stat_show_period) {
            chartType = CurrentChartType.PERIOD_BAR_CHART;
            setFromButton();
            return true;
        } else if (item.getItemId() == R.id.option_stat_show_pie) {
            chartType = CurrentChartType.PERIOD_PIE_CHART;
            setFromButton();
            return true;
        } else if (item.getItemId() == R.id.option_stat_show_day) {
            chartType = CurrentChartType.DAY_CHART;
            setFromButton();
            return true;
        } else return false;
    }

    public enum CurrentChartType {
        DAY_CHART, PERIOD_PIE_CHART, PERIOD_BAR_CHART
    }
}
