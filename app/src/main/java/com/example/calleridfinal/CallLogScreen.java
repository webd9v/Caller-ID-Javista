package com.example.calleridfinal;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CallLogScreen extends AppCompatActivity {
    ListView callsList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log_screen);
        callsList=findViewById(R.id.callsList);
        displayCallLog();
    }
    public void displayCallLog() {
        ArrayList<String> info = new ArrayList<>();

        String singleRecord = "";
        Uri uriCallLog = Uri.parse("content://call_log/calls");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Cursor cursorCallLogs = getContentResolver().query(uriCallLog, null, null, null);
            cursorCallLogs.moveToFirst();
            do {
                @SuppressLint("Range") String number = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER));
                @SuppressLint("Range") String name = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME));
                @SuppressLint("Range") String type = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE));
                @SuppressLint("Range") String duration = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION));
                if (type.equals("3") || type.equals("5")) {
                    type = "Unanswered";
                } else if (type.equals("1")) {
                    type = "Answered Incoming call";
                } else if (type.equals("2")) {
                    type = "Answered Outgoing call";

                }
                duration += "s";
                if (name == null) {
                    String testNbr=MainActivity.contactsByPhone.get(number);
                    if(testNbr==null){
                        name = "Unknown";
                    }else{
                        name=testNbr.split(":")[0];
                    }
                }
                singleRecord = "Number: " + number + "\nName: " + name + "\ntype: " + type + "\nDuration: " + duration + "\n";
                info.add(singleRecord);

            } while (cursorCallLogs.moveToNext());
            ArrayList<String> info1=new ArrayList<>();
            for(int i=info.size()-1;i>=0;i--){
                String call=info.get(i);
                info1.add(call);
            }
            info=info1;
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, info);
        callsList.setAdapter(arrayAdapter);
    }
}
