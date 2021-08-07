package ru.kurant.bellytracker.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import ru.kurant.bellytracker.R;
import ru.kurant.bellytracker.controller.DrinkList;
import ru.kurant.bellytracker.model.DrinkType;

public class AddOrEditBeverageDialogFragment extends DialogFragment implements View.OnClickListener {

    private DrinkList drinkList;
    private DrinkType beverageType;

    private NavController navController;

    private TextView idTextView;
    private AutoCompleteTextView nameTextView;
    private EditText alcoholTextView;
    private CheckBox hasSugarCheckBox;
    private CheckBox isHotCheckBox;
    private Button okButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_add_beverage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        drinkList = new DrinkList(getContext());
        navController = NavHostFragment.findNavController(this);


        initViews(view);

        int beverageId = AddOrEditBeverageDialogFragmentArgs.fromBundle(getArguments()).getBeverageIdArg();

        String beverageName = AddOrEditBeverageDialogFragmentArgs.fromBundle(getArguments()).getBeverageNameArg();

        if (beverageId > 0) {
            beverageType = drinkList.getDrinkTypeById(beverageId);
            loadFields();
        } else if (beverageName != null) {
            nameTextView.setText(beverageName);
        }
    }

    private void initViews(View view) {
        idTextView = view.findViewById(R.id.drink_type_id);
        nameTextView = view.findViewById(R.id.drink_type_name);
        alcoholTextView = view.findViewById(R.id.drink_type_alcohol);
        hasSugarCheckBox = view.findViewById(R.id.checkBox_sugar);
        isHotCheckBox = view.findViewById(R.id.checkBox_hot);
        okButton = view.findViewById(R.id.ok_add_edit_drink_type_btn);

        final ArrayAdapter<String> drinkNamesAdapter = new ArrayAdapter<>
                (getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        drinkList.getDrinkTypeNames());
        nameTextView.setAdapter(drinkNamesAdapter);
        nameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                drinkNamesAdapter.getDropDownView(i, view, adapterView);
            }
        });
        nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameTextView.showDropDown();
            }
        });
        okButton.setOnClickListener(this);
    }

    private void loadFields() {
        idTextView.setText(Integer.toString(beverageType.getDrinkTypeId()));
        nameTextView.setText(beverageType.getDrinkName());
        alcoholTextView.setText(Integer.toString(beverageType.getAlcoholPercent()));
        hasSugarCheckBox.setChecked(beverageType.isHasSugar());
        isHotCheckBox.setChecked(beverageType.isHot());
    }

    @Override
    public void onClick(View v) {
        String name = nameTextView.getText().toString().trim();
        String id = idTextView.getText().toString();

        if (name.equals("")) {
            nameTextView.setError("Please input beverage");
            return;
        } else if (drinkList.getDrinkTypeNames().contains(name)) {
            DrinkType beverageExist = drinkList.getDrinkType(name);
            if (id.equals("ID")) {
                String msg = name + ": " + getString(R.string.not_new_drinkType_msg);
                AlertDialog.Builder builder = new AlertDialog.Builder(nameTextView.getContext());
                builder.setMessage(msg).setPositiveButton(R.string.yes_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        beverageType = beverageExist;
                        loadFields();
                    }
                }).setNegativeButton(R.string.no_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), R.string.change_drink_name_msg, Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return;
            } else if (beverageExist.getDrinkTypeId() != Integer.parseInt(id)) {
                beverageType = beverageExist;
                loadFields();
            }
        }
        int alc;
        try {
            alc = Integer.parseInt(alcoholTextView.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            alcoholTextView.setError("Input alcohol %");
            return;
        }
        if (alc < 0 || alc > 98) {
            alcoholTextView.setError("Wrong number, must be in [0...98]");
        }
        boolean sugar = hasSugarCheckBox.isChecked();
        boolean hot = isHotCheckBox.isChecked();
        if (beverageType == null) {
            beverageType = new DrinkType(name, alc, sugar,hot);
            drinkList.addDrinkType(beverageType);
        } else {
            beverageType.setDrinkName(name);
            beverageType.setAlcoholPercent(alc);
            beverageType.setHasSugar(sugar);
            beverageType.setHot(hot);
            drinkList.updateDrinkType(beverageType);
        }
        navController
                .navigate(AddOrEditBeverageDialogFragmentDirections.actionBeverageAdded()
                .setBeverageName(beverageType.getDrinkName()));
    }
}
