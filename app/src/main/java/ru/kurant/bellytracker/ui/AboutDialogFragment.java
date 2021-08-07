package ru.kurant.bellytracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import ru.kurant.bellytracker.R;

public class AboutDialogFragment extends DialogFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Button okButton = view.findViewById(R.id.about_button);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        Toast.makeText(getContext(), "Bye Bye", Toast.LENGTH_SHORT).show();
    }
}
