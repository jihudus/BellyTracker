package ru.kurant.bellytracker.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.controller.DrinkList;
import ru.kurant.bellytracker.controller.share.StringPatterns;
import ru.kurant.bellytracker.model.Drink;
import ru.kurant.bellytracker.model.DrinkType;

public class AddOrEditDrinkFragment extends Fragment {

    private TextView drinkIdField;
    private EditText drinkTimeField;
    private AutoCompleteTextView drinkTypeTextView;
    private EditText drinkVolumeField;
    private Button addNewDrinkTypeBtn;
    private ImageButton timeButton;
    private FloatingActionButton okButton;

    private DrinkList drinkList;
    private Drink drink;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drinkList = new DrinkList(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_or_edit_drink, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {

        initViews(view);
        int drinkId = AddOrEditDrinkFragmentArgs.fromBundle(getArguments()).getDrinkIdArg();
        String beverageName = AddOrEditDrinkFragmentArgs.fromBundle(getArguments()).getBeverageName();
        String time = AddOrEditDrinkFragmentArgs.fromBundle(getArguments()).getTimeArg();
        if (drinkId != 0) {
            drink = drinkList.getDrinkById(drinkId);
            loadFields();
        } else if (beverageName != null) {
            drinkTypeTextView.setText(beverageName);
            loadCurrentTime();
        } else if (time != null) {
            drinkTimeField.setText(time);
        } else loadCurrentTime();
    }

    private void initViews(View view) {
        drinkIdField = view.findViewById(R.id.idTextView);
        drinkTimeField = view.findViewById(R.id.editDrinkTimeField);
        drinkTypeTextView = view.findViewById(R.id.drinkTypeTextView);
        drinkVolumeField = view.findViewById(R.id.inputVolumeField);
        addNewDrinkTypeBtn = view.findViewById(R.id.addNewDrinkTypeBtn);
        timeButton = view.findViewById(R.id.timeField_button);
        okButton = view.findViewById(R.id.ok_add_edit_drink_btn);

        attachSpinnerAdapter();

        initAddNewDrinkTypeBtn();

        initTimeButton();

        initOkButton();
    }

    private void attachSpinnerAdapter() {
        final ArrayAdapter<String> drinkTypeArrayAdapter = new ArrayAdapter<>
                (getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        drinkList.getDrinkTypeNames());
        drinkTypeTextView.setAdapter(drinkTypeArrayAdapter);
        AdapterView.OnItemClickListener drinkTypeClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                drinkTypeArrayAdapter.getDropDownView(i,view,adapterView);
            }
        };
        drinkTypeTextView.setOnItemClickListener(drinkTypeClickListener);
        drinkTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drinkTypeTextView.showDropDown();
            }
        });
    }

    private void initAddNewDrinkTypeBtn() {
        addNewDrinkTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOrEditDrinkFragmentDirections.ActionAddBeverage action = AddOrEditDrinkFragmentDirections.actionAddBeverage();
                if (!drinkTypeTextView.getText().toString().trim().equals("")) {
                    String beverageName = drinkTypeTextView.getText().toString().trim();
                    DrinkType beverageType = drinkList.getDrinkType(beverageName);
                    if (beverageType == null) {
                        action.setBeverageNameArg(beverageName);
                    } else {
                        int drinkTypeID = drinkList.getDrinkTypeId(beverageName);
                        action.setBeverageIdArg(drinkTypeID);
                    }
                }
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    private void initTimeButton() {
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddOrEditDrinkFragmentDirections.ActionTimePicker actionTimePicker =
                        AddOrEditDrinkFragmentDirections.actionTimePicker();
                SimpleDateFormat sdf = new SimpleDateFormat(StringPatterns.datePattern + " " + StringPatterns.timePattern, Locale.getDefault());
                String drinkFieldInput = drinkTimeField.getText().toString();
                Date date;
                try {
                    date = sdf.parse(drinkFieldInput);
                } catch (ParseException e) {
                    date = null;
                }
                if (date != null) {
                    actionTimePicker.setTimeAddedArg(drinkFieldInput);
                }
                Navigation.findNavController(view).navigate(actionTimePicker);
            }
        });
    }

    private void initOkButton() {
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check input date and time
                String drinkTimeInField = drinkTimeField.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat(StringPatterns.datePattern + " " + StringPatterns.timePattern, Locale.getDefault());
                Date date;
                String dateTime;
                try {
                    date = sdf.parse(drinkTimeInField);
                } catch (ParseException e) {
                    drinkTimeField.setError("Cannot parse the date");
                    return;
                }
                if (date != null) {
                    dateTime = StringPatterns.convertTimeForDB(drinkTimeInField);
                } else {
                    drinkTimeField.setError("Date is Null");
                    return;
                }

                // Check if beverage name exists in db (otherwise show the msg)
                String drinkTypeInField = drinkTypeTextView.getText().toString();
                DrinkType beverageType = drinkList.getDrinkType(drinkTypeInField);
                if (beverageType == null) {
                    drinkTypeTextView.setError("Cannot find in database");
                    return;
                }

                // Try to parse double from input field. If parsing fails, show error msg
                double drinkVolume;
                try {
                    drinkVolume = Double.parseDouble(drinkVolumeField.getText().toString());
                } catch (NumberFormatException nfe) {
                    drinkVolumeField.setError("Cannot parse volume");
                    return;
                }

                // Add new drink case or edit existing
                if (drink == null) {
                    drink = new Drink(dateTime, beverageType.getDrinkTypeId(), drinkVolume);
                    drinkList.addDrink(drink);
                }
                else {
                    drink.setDrinkTime(dateTime);
                    drink.setDrinkType(beverageType.getDrinkTypeId());
                    drink.setVolume(drinkVolume);
                    drinkList.updateDrink(drink);
                }
                Navigation.findNavController(view).navigate(R.id.actionDrinkAdded);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu_add_drink, menu);
    }

    private void loadFields() {
        drinkIdField.setText(Integer.toString(drink.getId()));
        drinkTimeField.setText(StringPatterns.convertTimeForUser(drink.getDrinkTime()));
        drinkVolumeField.setText(Double.toString(drink.getVolume()));
        drinkTypeTextView.setText(drinkList.getDrinkTypeById(drink.getDrinkType()).getDrinkName());
    }

    private void loadCurrentTime() {
        SimpleDateFormat currentTime = new SimpleDateFormat(StringPatterns.datePattern + " " + StringPatterns.timePattern, Locale.getDefault());
        drinkTimeField.setText(currentTime.format(new Date()));
    }
}