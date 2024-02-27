package com.testingapp.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogGetGraphics extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_get_data,container,false);
        Button btn1 = view.findViewById(R.id.btn_date);
        btn1.setOnClickListener(v -> {
            DataPickerFragment fragment = new DataPickerFragment();
            fragment.show(getActivity().getSupportFragmentManager(),"datepicker" );
            dismiss();
        });
        Button btn2 = view.findViewById(R.id.btn_time);
        btn2.setOnClickListener(v -> {
            TimePickerFragment fragment = new TimePickerFragment();
            fragment.show(getActivity().getSupportFragmentManager(),"datepicker" );
            dismiss();
        });

        return view;
    }
}
