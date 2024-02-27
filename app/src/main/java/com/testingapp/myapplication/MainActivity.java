package com.testingapp.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    BluetoothManager manager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner scanner;
    List<BluetoothDevice> list;
    ListView listView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission();
        init();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = list.get(position);
                Intent intent = new Intent(MainActivity.this, BluetoothConnectActivity.class);
                intent.putExtra("device", device.getAddress());
                startActivity(intent);
            }
        });
    }
    private void init(){
        list = new ArrayList<>();
        manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();
        scanner = bluetoothAdapter.getBluetoothLeScanner();
        listView = findViewById(R.id.listview);
        adapter = new Adapter(this,R.layout.list_item,list);
        listView.setAdapter(adapter);
    }
    private void permission(){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 1);
    }
    private ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();

            if(!list.contains(device)){
                if(device.getName() != null) {
                    list.add(device);
                }
            }
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }
        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    @SuppressLint("MissingPermission")
    public void click(View view){
        list.clear();
        adapter.notifyDataSetChanged();
        if(bluetoothAdapter.isEnabled()) {

            scanner.startScan(scanCallback);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanner.stopScan(scanCallback);
                }
            }, 5000);
        }
        else {
            Toast.makeText(MainActivity.this,"Включите блюзут!",Toast.LENGTH_SHORT).show();
        }
    }


}