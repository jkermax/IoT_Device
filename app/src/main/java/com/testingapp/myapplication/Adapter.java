package com.testingapp.myapplication;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Adapter extends ArrayAdapter<BluetoothDevice> {
    private LayoutInflater inflater;
    private int layout;
    private List<BluetoothDevice> deviceList;

    public Adapter(Context context, int resource, List<BluetoothDevice> deviceList) {
        super(context, resource, deviceList);
        this.deviceList = deviceList;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=inflater.inflate(this.layout, parent, false);

        TextView nameView = view.findViewById(R.id.info);

        BluetoothDevice device = deviceList.get(position);

        nameView.setText(device.getName() + "\n" + device.getAddress());

        return view;
    }
}