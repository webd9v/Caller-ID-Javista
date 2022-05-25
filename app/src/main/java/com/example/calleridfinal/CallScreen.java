package com.example.calleridfinal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CallScreen extends AppCompatActivity {
    TextView outputContactInfo;
//    Button addContactInDynamics;
    ImageView exitCallScreen;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_screen);
        Bundle bundle=this.getIntent().getExtras();
        String number=bundle.getString("phoneNumber");
        exitCallScreen=findViewById(R.id.exitCallScreen);
        exitCallScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        boolean isFound=bundle.getBoolean("isFound");
//        addContactInDynamics=findViewById(R.id.addContactDynamics);
        outputContactInfo=(TextView) findViewById(R.id.outputContactInfo);
        if(!isFound) {
            outputContactInfo.setText("Incoming call from: " + number);
            System.out.println("isFound:"+isFound+" number:"+number);
//            addContactInDynamics.setVisibility(View.VISIBLE);
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
