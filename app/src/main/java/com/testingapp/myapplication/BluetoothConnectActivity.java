package com.testingapp.myapplication;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

public class BluetoothConnectActivity extends AppCompatActivity implements DialogSettingsFragment.OnDataChangeListener {
    BluetoothGatt bluetoothGatt;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device;
    String adress,test;
    TextView txt;
    BluetoothGattService services;
    List<BluetoothGattCharacteristic> characteristics;
    Button btn_wifi,btn_settings,btn_graphics,btn_back;
    LinearLayout base_info,set_base_info,wifi_conn,ip_sql;
    Intent intent;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connect_activity);
        init();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        adress = getIntent().getStringExtra("device");
        device = bluetoothAdapter.getRemoteDevice(adress);
        bluetoothGatt = device.connectGatt(this,false,gattCallback,BluetoothDevice.TRANSPORT_LE);

        txt = findViewById(R.id.info);

    }

    private void init(){

        btn_wifi = findViewById(R.id.Connect_WiFi);
        btn_settings = findViewById(R.id.Settings);
        btn_graphics = findViewById(R.id.Graphics);
        base_info = findViewById(R.id.layout_base);
        wifi_conn = findViewById(R.id.wifi_conn);
        btn_back = findViewById(R.id.back_btn);
        ip_sql = findViewById(R.id.ip_layout);
        set_base_info = findViewById(R.id.layout_base_info);
        intent = new Intent(BluetoothConnectActivity.this, GetActivity.class);

    }
    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                            gatt.discoverServices();

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    txt.setText("Подключитесь заново к устройству");
                    break;
            }

        }
        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {

                for(BluetoothGattService service: gatt.getServices()) {
                    if (!service.getUuid().toString().startsWith("000018"))
                    {   services = gatt.getService(service.getUuid());
                        characteristics  = services.getCharacteristics();
                    }
                }
                runOnUiThread(() -> {
                    btn_settings.setVisibility(View.VISIBLE);
                    btn_graphics.setVisibility(View.VISIBLE);
                    BluetoothGattCharacteristic characteristic = characteristics.get(2);
                    bluetoothGatt.readCharacteristic(characteristic);
                });
            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            byte[] value1 = characteristic.getValue();
            test = new String(value1, StandardCharsets.UTF_8);

            if(!test.equals("boob") && !characteristic.equals(characteristics.get(2)))
            {
                SQL_Class sqlClass = new SQL_Class(getApplicationContext());
                if(sqlClass.SQL_connect()) {
                   int a = sqlClass.sql_device(device.getAddress());

                   intent.putExtra("ip", test);
                   intent.putExtra("id_device", a);
                   startActivity(intent);
                }
                else{
                    runOnUiThread(()-> Toast.makeText(BluetoothConnectActivity.this,"Устройство не подключается к SQL",Toast.LENGTH_SHORT).show());
                }
            }
            else if(characteristic.equals(characteristics.get(2)))
            {

                runOnUiThread(() -> {
                    try {
                        String[] mass = test.split(",");
                        txt.setText(MessageFormat.format("SN:{0}\tЦех:{1}\tЗавод:{2}", mass[0], mass[1], mass[2]));
                    }
                    catch (Exception e){}
                    });

            }
            else {
                runOnUiThread(() -> Toast.makeText(BluetoothConnectActivity.this,"Устройство не подключено к WiFi",Toast.LENGTH_SHORT).show());

            }
        }
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }
        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
        }
    };
    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothGatt.disconnect();
    }
    @SuppressLint("MissingPermission")
    public void Connect_WiFi(View view) {

        EditText login,password;
        login = findViewById(R.id.Login);
        password = findViewById(R.id.Password);

        if(!login.getText().equals("") && !password.getText().equals("") ) {
            String wifi = login.getText() + "," + password.getText();

            try {
                BluetoothGattCharacteristic characteristic = characteristics.get(0);
                characteristic.setValue(wifi);
                bluetoothGatt.writeCharacteristic(characteristic);
                LinearLayout wifi_conn = findViewById(R.id.wifi_conn);
                wifi_conn.setVisibility(View.GONE);
                btn_graphics.setVisibility(View.VISIBLE);
                btn_settings.setVisibility(View.VISIBLE);
                base_info.setVisibility(View.VISIBLE);
                btn_back.setVisibility(View.GONE);

            } catch (Exception e) {
            }
            try {
                Thread.sleep(1000);
                BluetoothGattCharacteristic characteristic1 = characteristics.get(2);
                bluetoothGatt.readCharacteristic(characteristic1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            Toast.makeText(BluetoothConnectActivity.this,"Введите название сети и пароль!",Toast.LENGTH_SHORT).show();
        }

    }
    public void show(View view) {
        wifi_conn.setVisibility(View.VISIBLE);
        btn_wifi.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    public void click(View view) {

    }

    @SuppressLint("MissingPermission")
    public void send_info(View view) {
        EditText sn, ceh, zavod;
        sn = findViewById(R.id.SN);
        ceh = findViewById(R.id.Ceh);
        zavod = findViewById(R.id.Zavod);
        if (!sn.getText().equals("") || !ceh.getText().equals("") || !zavod.getText().equals("")) {
            String info = MessageFormat.format("{0},{1},{2}", sn.getText(), ceh.getText(), zavod.getText());
            BluetoothGattCharacteristic characteristic = characteristics.get(2);
            characteristic.setValue(info);
            bluetoothGatt.writeCharacteristic(characteristic);
            set_base_info.setVisibility(View.GONE);
            btn_graphics.setVisibility(View.VISIBLE);
            btn_settings.setVisibility(View.VISIBLE);
            base_info.setVisibility(View.VISIBLE);
            try {
                Thread.sleep(1000);
                BluetoothGattCharacteristic characteristic1 = characteristics.get(2);
                bluetoothGatt.readCharacteristic(characteristic1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            BluetoothGattCharacteristic characteristic1 = characteristics.get(2);
            bluetoothGatt.readCharacteristic(characteristic1);
        }
        else {
        Toast.makeText(this, "Введите что то одно", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onDataChanged(int setting) {

        btn_settings.setVisibility(View.GONE);
        btn_graphics.setVisibility(View.GONE);
        base_info.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        switch (setting){

            case 0:
                btn_wifi.setVisibility(View.VISIBLE);
                break;
            case 1:
                set_base_info.setVisibility(View.VISIBLE);
                break;
            case 2:
                ip_sql.setVisibility(View.VISIBLE);
                break;

        }
    }

    public void settings(View view) {
        DialogSettingsFragment dialogSettingsFragment = new DialogSettingsFragment();
        dialogSettingsFragment.show(getSupportFragmentManager(),"852");
    }

    @SuppressLint("MissingPermission")
    public void Graphics(View view) {
        BluetoothGattCharacteristic characteristic1 = characteristics.get(1);
        bluetoothGatt.readCharacteristic(characteristic1);
    }

    public void back(View view) {

        btn_wifi.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        set_base_info.setVisibility(View.GONE);
        ip_sql.setVisibility(View.GONE);
        wifi_conn.setVisibility(View.GONE);
        btn_graphics.setVisibility(View.VISIBLE);
        btn_settings.setVisibility(View.VISIBLE);
        base_info.setVisibility(View.VISIBLE);

    }

    @SuppressLint("MissingPermission")
    public void set_ip(View view) {
        EditText sql_ip = findViewById(R.id.ip_text);
        if(!sql_ip.getText().equals("")) {
            btn_graphics.setVisibility(View.VISIBLE);
            btn_settings.setVisibility(View.VISIBLE);
            base_info.setVisibility(View.VISIBLE);
            btn_back.setVisibility(View.GONE);
            ip_sql.setVisibility(View.GONE);
            BluetoothGattCharacteristic characteristic = characteristics.get(2);
            bluetoothGatt.readCharacteristic(characteristic);

            SQL_Class sqlClass = new SQL_Class(getApplicationContext());
            sqlClass.setIpAddress(sql_ip.getText().toString());
        }
    }
}
