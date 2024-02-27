package com.testingapp.myapplication;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_pick,container,false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TimePicker startTimePicker = view.findViewById(R.id.start_time_picker);
        startTimePicker.setIs24HourView(true);
        TimePicker endTimePicker = view.findViewById(R.id.end_time_picker);
        endTimePicker.setIs24HourView(true);

        Button okButton = view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка нажатия на кнопку OK
                // Получите выбранные значения времени начала и окончания
                SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
                String time_start = sdf.format(new Date(0,0,0,startTimePicker.getHour(),0)) + ":" + startTimePicker.getMinute() + ":00";
                String time_end = sdf.format(new Date(0,0,0,endTimePicker.getHour(),0)) + ":" + endTimePicker.getMinute() + ":00";


                if (getActivity() instanceof TimePickerFragment.getTimeInterface) {
                    ((TimePickerFragment.getTimeInterface) getActivity()).getTime(time_start,time_end);
                    dismiss();
                }

                dismiss(); // Закрыть диалоговое окно
            }
        });
    }
    public interface getTimeInterface{
        void getTime(String date_start,String date_end);

    }
}
