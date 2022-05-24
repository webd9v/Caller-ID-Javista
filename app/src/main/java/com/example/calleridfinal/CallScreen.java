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
            String address=bundle.getString("address");
            String email=bundle.getString("email");
            if(address==null){
                address="No information";
            }
            if(email==null){
                email="No information";
            }
            outputContactInfo.setText(fullname+"\n"+number+"\n"+"Address: "+address+"\n"+"Email: "+email);
            System.out.println("isFound:"+isFound+" number:"+number+" name:"+fullname);

        }
    }
}
