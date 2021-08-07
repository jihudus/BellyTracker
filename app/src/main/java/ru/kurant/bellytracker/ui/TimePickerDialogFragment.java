package ru.kurant.bellytracker.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.controller.share.StringPatterns;

public class TimePickerDialogFragment extends DialogFragment {

    DatePicker datePicker;
    TimePicker timePicker;
    Button okButton;

    NavController navController;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_date_time_picker, container);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        navController = NavHostFragment.findNavController(this);

        okButton = view.findViewById(R.id.time_date_ok_button);
        datePicker = view.findViewById(R.id.date_picker);
        timePicker = view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        okButton.setOnClickListener(okBtnListener);

        String savedTime = TimePickerDialogFragmentArgs.fromBundle(getArguments()).getTimeAddedArg();
        Date date;
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        if (savedTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(StringPatterns.datePattern + " " + StringPatterns.timePattern, Locale.getDefault());
            try {
                date = sdf.parse(savedTime);
                cal.setTime(date);
            } catch (Exception e) {
                cal.setTime(Calendar.getInstance().getTime());
            }
        } else cal.setTime(Calendar.getInstance().getTime());

        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(cal.get(Calendar.MINUTE));
        } else {
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        }
    }

    private View.OnClickListener okBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int year = datePicker.getYear();
            int month = datePicker.getMonth();
            int day = datePicker.getDayOfMonth();
            int hour = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ?
                    timePicker.getHour() : timePicker.getCurrentHour();
            int minute = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ?
                    timePicker.getMinute() : timePicker.getCurrentMinute();
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute);
            SimpleDateFormat sdf = new SimpleDateFormat(StringPatterns.datePattern + " " + StringPatterns.timePattern, Locale.getDefault());
            String dateTimeString = sdf.format(calendar.getTime());
            navController.navigate(TimePickerDialogFragmentDirections.actionTimeChosen().setTimeArg(dateTimeString));
        }
    };
}