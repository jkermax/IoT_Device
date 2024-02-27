package com.testingapp.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQL_Class {

    private  String ip ;
    private static String IP_KEY = "ip_key";
    private static String  port= "61344";
    private static String Clases = "net.sourceforge.jtds.jdbc.Driver";
    private static String database = "IoT_device";
    private static String user = "connect";
    private static  String password = "123456";
    private static  String url ;
    private Connection connection = null;
    private Context context;

    public SQL_Class(Context context) {
        this.context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences("SQL_Preferences", Context.MODE_PRIVATE);
        ip = sharedPreferences.getString(IP_KEY, "192.168.5.67");
        url = "jdbc:jtds:sqlserver://" + ip + ":" + port + ":" + database;
    }

    public boolean SQL_connect(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        boolean connect = false;
        try {
            Class.forName(Clases);
            connection = DriverManager.getConnection(url,user,password);
            Log.d("SQL_CONNECT","CONNECTED");
            connect = true;
        } catch (ClassNotFoundException e) {
            Log.e("Class not FOund",e.getMessage());
            connect = false;
        } catch (SQLException e) {
            Log.e("CXZCXCX",e.getMessage());
            connect = false;
        }
        return connect;
    }
    public int sql_device(String mac){
        int a=1;
        if(connection!=null){
            try {
                String checkQuery = "use "+ database+" \n SELECT * FROM Devices WHERE MAC = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, mac );
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    String insertQuery ="use "+ database+" \n INSERT INTO Devices (MAC,SN_engine) VALUES (?,54655)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, mac);
                    insertStatement.executeUpdate();

                    String query = "use "+ database+" \n SELECT * FROM Devices WHERE MAC = ?";
                    PreparedStatement Statement = connection.prepareStatement(query);
                    Statement.setString(1, mac );
                    ResultSet resultSet1 = checkStatement.executeQuery();
                    a = resultSet1.getInt(2);
                }
                else{
                    a = resultSet.getInt(1);
                    Log.i("MAC", String.valueOf(resultSet.getInt(1)));

                }
            } catch (SQLException e) {
                Log.e("852",e.getMessage());
            }
        }
        return a;
    }
    public void sql_temp(List<datas> datas,int id_device){

        for(datas datas1 : datas){
        if(connection!=null) {
            try {
                String checkQuery = "use " + database + " \n SELECT * FROM Temperature WHERE Time = ? AND Date = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, datas1.getTime());
                checkStatement.setString(2, datas1.getDate());
                ResultSet resultSet = checkStatement.executeQuery();

                if (!resultSet.next()) {
                    String insertQuery = "use " + database + " \n INSERT INTO Temperature (Date,Time,Temperature,ID_device) VALUES (?,?,?,?)";
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setString(1, datas1.getDate());
                    insertStatement.setString(2, datas1.getTime());
                    insertStatement.setString(3, datas1.getData());
                    insertStatement.setInt(4, id_device);
                    insertStatement.executeUpdate();
                    Log.d("SQL_data","WRITING TEMP");
                }
                else{

                    Log.d("SQL_data","NOT WRITING TEMP");
                }
            } catch (SQLException e) {
                Log.e("852", e.getMessage());
            }
        }
        }
    }
    public void sql_vibro(List<datas> datas,int id_device) {
        if(connection!=null){
            for(datas datas1 : datas) {
                try {
                    String checkQuery = "use " + database + " \n SELECT * FROM Vibration WHERE Time = ? AND Date = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setString(1, datas1.getTime());
                    checkStatement.setString(2, datas1.getDate());
                    ResultSet resultSet = checkStatement.executeQuery();

                    if (!resultSet.next()) {
                        String insertQuery = "use " + database + " \n INSERT INTO Vibration (Date,Time,Vibration,ID_device) VALUES (?,?,?,?)";
                        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                        insertStatement.setString(1, datas1.getDate());
                        insertStatement.setString(2, datas1.getTime());
                        insertStatement.setString(3, datas1.getData());
                        insertStatement.setInt(4, id_device);
                        insertStatement.executeUpdate();
                        Log.d("SQL_data","WRITING VIBRO");
                    }
                    else{

                        Log.d("SQL_data","NOT WRITING VIBRO");
                    }
                } catch (SQLException e) {
                    Log.e("852", e.getMessage());
                }
            }
        }
    }
    public void sql_magnetic(List<datas> datas,int id_device){
        if(connection!=null) {
            for (datas datas1 : datas) {
                try {
                    String checkQuery = "use " + database + " \n SELECT * FROM Magnetic WHERE Time = ? AND Date = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setString(1, datas1.getTime());
                    checkStatement.setString(2, datas1.getDate());
                    ResultSet resultSet = checkStatement.executeQuery();


                    if (!resultSet.next()) {
                        String insertQuery = "use " + database + " \n INSERT INTO Magnetic (Date,Time,Magnetic,ID_device) VALUES (?,?,?,?)";
                        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                        insertStatement.setString(1, datas1.getDate());
                        insertStatement.setString(2, datas1.getTime());
                        insertStatement.setString(3, datas1.getData());
                        insertStatement.setInt(4, id_device);
                        insertStatement.executeUpdate();
                        Log.d("SQL_data","WRITING MAGNETIC");
                    }
                    else{

                        Log.d("SQL_data","NOT WRITING MAGNETIC");
                    }
                } catch (SQLException e) {
                    Log.e("852", e.getMessage());
                }
            }
        }
    }
    public List<SQL_data> sql_get_data(int table, String Data, String time_start, String time_end) {
        List<SQL_data> dataList = new ArrayList<>();
        if (connection != null) {
        switch (table) {
            case 0:
                try {
                String checkQuery = "use " + database + " \n SELECT * FROM Temperature WHERE Date = ? AND Time BETWEEN ? AND ? ";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, Data);
                checkStatement.setString(2, time_start);
                checkStatement.setString(3, time_end);
                ResultSet resultSet = checkStatement.executeQuery();

                while (resultSet.next()){
                    SQL_data sql_data = new SQL_data();
                    sql_data.setTime(resultSet.getString("Time"));
                    sql_data.setData(resultSet.getString("Temperature"));
                    dataList.add(sql_data);
                 }

                 }
                catch (SQLException e) {
                Log.e("852", e.getMessage());

                }
                break;
            case 1:
                try {
                    String checkQuery = "use " + database + " \n SELECT * FROM Magnetic WHERE Date = ? AND Time BETWEEN ? AND ? ";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setString(1, Data);
                    checkStatement.setString(2, time_start);
                    checkStatement.setString(3, time_end);
                    ResultSet resultSet = checkStatement.executeQuery();
                    while (resultSet.next()){
                        SQL_data sql_data = new SQL_data();
                        sql_data.setTime(resultSet.getString("Time"));
                        sql_data.setData(resultSet.getString("Magnetic"));
                        dataList.add(sql_data);
                    }
                }
                catch (SQLException e) {
                    Log.e("852", e.getMessage());

                }
                break;
            case 2:
                try {
                    String checkQuery = "use " + database + " \n SELECT * FROM Vibration WHERE Date = ? AND Time BETWEEN ? AND ? ";
                    PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                    checkStatement.setString(1, Data);
                    checkStatement.setString(2, time_start);
                    checkStatement.setString(3, time_end);
                    ResultSet resultSet = checkStatement.executeQuery();

                    while (resultSet.next()){
                        SQL_data sql_data = new SQL_data();
                        sql_data.setTime(resultSet.getString("Time"));
                        sql_data.setData(resultSet.getString("Vibration"));
                        dataList.add(sql_data);
                    }

                }
                catch (SQLException e) {
                    Log.e("852", e.getMessage());

                }
                break;
            }
        }
        return dataList;

    }
    public void setIpAddress(String newIp) {
        ip = newIp;
        url = "jdbc:jtds:sqlserver://" + ip + ":" + port + ":" + database;

        SharedPreferences sharedPreferences = context.getSharedPreferences("SQL_Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IP_KEY, ip);
        editor.apply();
    }
}