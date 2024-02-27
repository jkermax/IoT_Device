package com.testingapp.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogSettingsFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_activity,container,false);
        Button btn_WiFi = view.findViewById(R.id.WiFi);
        Button btn_info = view.findViewById(R.id.Base_Info);
        Button btn_sql = view.findViewById(R.id.btn_sql);

        btn_WiFi.setOnClickListener(v -> {
            if (getActivity() instanceof OnDataChangeListener) {
                ((OnDataChangeListener) getActivity()).onDataChanged(0);
                dismiss();
            }

        });
        btn_info.setOnClickListener(v -> {
            if (getActivity() instanceof OnDataChangeListener) {
                ((OnDataChangeListener) getActivity()).onDataChanged(1);
                dismiss();
            }
        });
        btn_sql.setOnClickListener(v -> {
            if (getActivity() instanceof OnDataChangeListener) {
                ((OnDataChangeListener) getActivity()).onDataChanged(2);
                dismiss();
            }
        });
        return view;

    }

    public interface OnDataChangeListener {
        void onDataChanged(int setting);
    }


}
