package com.example.calleridfinal;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CallScreen extends AppCompatActivity {
    TextView outputContactInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_screen);
        Bundle bundle=this.getIntent().getExtras();
        String number=bundle.getString("phoneNumber");
        boolean isFound=bundle.getBoolean("isFound");

        outputContactInfo=(TextView) findViewById(R.id.outputContactInfo);
        if(!isFound) {
            outputContactInfo.setText("Incoming call from: " + number);
            System.out.println("isFound:"+isFound+" number:"+number);
        }else{
            String fullname=bundle.getString("fullname");
            outputContactInfo.setText("Incoming call from "+fullname+".\nCaller's number is: "+number);
            System.out.println("isFound:"+isFound+" number:"+number+" name:"+fullname);

        }
    }
}
