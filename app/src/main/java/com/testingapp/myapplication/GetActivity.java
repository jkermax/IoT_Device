package com.testingapp.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GetActivity extends AppCompatActivity  implements DataPickerFragment.getDataInterface, TimePickerFragment.getTimeInterface {

    //Объявление переменных
    OkHttpClient client;
    List<datas> list_temp,list_magnet,list_vibro,list_FFT;
    String[] mass;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    Date date;
    int id_device;
    SQL_Class sqlClass;
    Spinner spinner;
    String value,temp,magnet,vibro,addres,current_date,time_start,time_end;


    //Инициализация приложения
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlClass =  new SQL_Class(getApplicationContext());
        sqlClass.SQL_connect();
        setContentView(R.layout.get_activity);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");

        current_date = sdf.format(calendar.getTime());
        time_start = "00:00:00";
        time_end = "23:59:59";


        mass = new String[]{"temp", "magnet", "vibro","FFT"};

        client = new OkHttpClient().newBuilder().readTimeout(60,TimeUnit.SECONDS).build();

        spinner = findViewById(R.id.spinner);

        String IP = getIntent().getStringExtra("ip");
        addres = "http://" + IP +"/";
        Log.i("IP",addres);
        id_device = getIntent().getIntExtra("id_device",1);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mass);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void getData(String date) {
        current_date = date;
        Log.d("Data",current_date);

    }
    @Override
    public void getTime(String date_start,String date_end) {
        time_start = date_start;
        time_end = date_end;

        Log.d("Data",time_start +"\t" + time_end);

    }
    //GET-запрос на получение данных температуры
    private void Get_temp(){

        list_temp = new ArrayList<>(10000000);

        Request request = new Request.Builder()
                .url(addres+"temp")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message());
                    }
                    temp = responseBody.string();
                    String[]mass = temp.split("\\r\\n");

                    for(int i = 0;i< mass.length;i++){
                        String[]mass1 = mass[i].split(",");
                        datas datas = new datas();
                        datas.setDate(mass1[0]);
                        datas.setTime(mass1[1]);
                        datas.setData(mass1[2]);
                        list_temp.add(datas);

                    }
                    sqlClass.sql_temp(list_temp,id_device);

                }
            }
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.println(Log.ERROR,"FAIL",e.getMessage());
            }
        });
    }
    //GET-запрос на получение данных магнитного поля
    private void Get_magnet(){

        list_magnet = new ArrayList<>(100000);

        Request request = new Request.Builder()
                .url(addres+ "magnet")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message());
                    }
                    magnet = responseBody.string();
                    String[]mass = magnet.split("\\n");
                    for(int i = 0;i< mass.length;i++){
                        String[]mass1 = mass[i].split(",");
                        datas datas = new datas();
                        datas.setDate(mass1[0]);
                        datas.setTime(mass1[1]);
                        datas.setData(mass1[2]);
                        list_magnet.add(datas);

                    }
                    sqlClass.sql_magnetic(list_magnet,id_device);
                }
            }
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.println(Log.ERROR,"FAIL",e.getMessage());
            }
        });
    }
    private void Get_vibro(){

        list_vibro = new ArrayList<>(10000000);

        Request request = new Request.Builder()
                .url(addres+ "vibro")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message());
                    }
                    vibro = responseBody.string();
                    String[]mass = vibro.split("\\n");

                    for(int i = 1;i< mass.length-1;i++){
                        String[]mass1 = mass[i].split(",");
                        datas datas = new datas();
                        datas.setDate(mass1[0]);
                        datas.setTime(mass1[1]);
                        datas.setData(mass1[3]);
                        list_vibro.add(datas);
                    }
                    sqlClass.sql_vibro(list_vibro,id_device);
                }
            }
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.println(Log.ERROR,"FAIL",e.getMessage());
            }
        });
    }
    private void Get_FFT(){

        list_FFT = new ArrayList<>();

        Request request = new Request.Builder()
                .url(addres+ "fft")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Запрос к серверу не был успешен: " +
                                response.code() + " " + response.message());
                    }
                    String fft = responseBody.string();
                    String[]mass = fft.split("\\n");
                    datas datas = new datas();
                    datas.setDate("0");
                    datas.setTime("v");
                    datas.setData("0");
                    list_FFT.add(datas);
                    for(int i = 1;i< mass.length-1;i++){
                        String[]mass1 = mass[i].split(",");
                        datas =new datas();
                        datas.setDate(mass1[0]);
                        datas.setTime("v");
                        datas.setData(mass1[1]);
                        list_FFT.add(datas);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.println(Log.ERROR,"FAIL",e.getMessage());
            }
        });
    }
    //Вывод графика с полученными данными
    private void Set_grapsh(List<SQL_data> list){

        double[] xValues = new double[list.size()];
        double[] yValues = new double[list.size()];

        for (int i=0;i<list.size();i++){
            try {

                date = sdf.parse(list.get(i).getTime());
                long timestamp = date.getTime();
                xValues[i]= timestamp;
                yValues[i]= Float.parseFloat((list.get(i).getData()));

            } catch (ParseException e) {
                Log.d("VSSDFVCX",e.getMessage());
            }

        }
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            entries.add(new Entry((float) xValues[i],(float) yValues[i]));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        LineChart chart = findViewById(R.id.chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisLineColor(Color.parseColor("#082567"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis1 = chart.getAxisRight();
        yAxis1.setAxisLineColor(Color.parseColor("#cd7f32"));
        yAxis1.setEnabled(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return sdf.format(new Date((long) value));
            }
        });

        chart.setData(lineData);
        chart.invalidate();
    }
    private void Set_FFT(List<datas> list){

        float[] xValues = new float[list.size()];
        float[] yValues = new float[list.size()];

        for (int i=0;i<list.size();i++){
            xValues[i]= Float.parseFloat((list.get(i).getDate()));
            yValues[i]= Float.parseFloat((list.get(i).getData()));
        }

        ArrayList<Entry> entrie = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            entrie.add(new Entry(xValues[i],yValues[i]));
        }
        LineDataSet dataSet = new LineDataSet(entrie, "FFT");
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);

        LineChart chart = findViewById(R.id.chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf(value);
            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxis1 = chart.getAxisRight();
        yAxis1.setEnabled(false);

        chart.setData(lineData);
        chart.invalidate();
    }
    //OnClick кнопки для получения данных
    public void Get_data(View view) {
        ExecutorService service = Executors.newFixedThreadPool(3);

        service.submit(new Runnable() {
            @Override
            public void run() {
                Get_temp();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                Get_vibro();
            }
        });
        service.submit(new Runnable() {
            @Override
            public void run() {
                Get_magnet();
            }
        });
        service.shutdown();
        Get_FFT();


    }
    //OnClick кнопки для вывода данных на график
    public void Get_data1(View view) {

        switch (value){
            case "temp":
                if(sqlClass.SQL_connect()) {
                    List<SQL_data> sql_data;
                    sql_data = sqlClass.sql_get_data(0, current_date, time_start, time_end);
                    Set_grapsh(sql_data);
                }
                break;
            case "magnet":
                if(sqlClass.SQL_connect()) {
                    List<SQL_data> sql_data;
                    sql_data = sqlClass.sql_get_data(1, current_date, time_start, time_end);
                    Set_grapsh(sql_data);
                }
                break;
            case "vibro":
                if(sqlClass.SQL_connect()) {
                    List<SQL_data> sql_data;
                    sql_data = sqlClass.sql_get_data(2, current_date, time_start, time_end);
                    Set_grapsh(sql_data);
                }
                break;
            case "FFT":
                Set_FFT(list_FFT);
                break;
            default:
                break;
        }

    }
    public void diapozon(View view) {
        DialogGetGraphics dialogGetGraphics = new DialogGetGraphics();
        dialogGetGraphics.show(getSupportFragmentManager(),"dialog");

    }


}