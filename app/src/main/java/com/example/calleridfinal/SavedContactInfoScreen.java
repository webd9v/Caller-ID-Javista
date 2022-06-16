package com.example.calleridfinal;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SavedContactInfoScreen extends AppCompatActivity {
    ImageView exitCallScreen;
    TextView firstnameField,lastnameField,emailField,phoneNumberField,addressField;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_contact_info_screen);
        Bundle bundle=this.getIntent().getExtras();
        String name=bundle.getString("name");
        String number=bundle.getString("phoneNumber");
        String email=bundle.getString("email");
        String address=bundle.getString("address");
        System.out.println("Name: "+name+"\n Number: "+number+"\nEmail: "+email+"\nAddress: "+address);
        ArrayList<String> ids= (ArrayList<String>) bundle.get("ids");
        HashMap<String,String> emails= (HashMap<String, String>) bundle.get("emails");
        phoneNumberField=findViewById(R.id.numberField);
        phoneNumberField.setText(number);
        exitCallScreen=findViewById(R.id.exitCallScreen);
        exitCallScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firstnameField=findViewById(R.id.firstnameField);
        lastnameField=findViewById(R.id.lastnameField);
        emailField=findViewById(R.id.emailField);

        addressField=findViewById(R.id.addressField);
        firstnameField.setText(name.split(" ")[0]);
        lastnameField.setText(name.split(" ")[1]);
        emailField.setText(email);
        if(address==null || address.equals("null")){
            addressField.setText("Unknown");
            addressField.setVisibility(View.GONE);
            TextView addressText=findViewById(R.id.addressText);
            addressText.setVisibility(View.GONE);
        }else{
            addressField.setText(address);

        }
        if(number==null || number.equals("null")){
            phoneNumberField.setText("Unknown");
            phoneNumberField.setVisibility(View.GONE);
            TextView phoneText=findViewById(R.id.phoneText);
            phoneText.setVisibility(View.GONE);
        }
//        TextView emailsDetail=findViewById(R.id.emails);
        ListView emailsListView=findViewById(R.id.emails);
        ArrayList<String> emailsArrayList;
        ArrayAdapter<String> emailsArrayAdapter;
        LinearLayout l1=findViewById(R.id.linearL1);
        String textViewText="";
        if(!emails.isEmpty()) {
            if (emails.get(ids.get(0)) != null) {
                emailsArrayList=new ArrayList<String>();
                emailsArrayAdapter=new ArrayAdapter<>(this,R.layout.saved_contact_list_item,emailsArrayList);
                emailsListView.setAdapter(emailsArrayAdapter);
                String dateReceived, body, subject;
                for (int i = 0; i < emails.size(); i++) {
                    String wholeInfo = emails.get(ids.get(i));
                    dateReceived = wholeInfo.split(":%")[0];
                    dateReceived=dateReceived.replace("T"," ");
                    dateReceived=dateReceived.replace("Z"," ");
                    subject = wholeInfo.split(":%")[1];
                    body = wholeInfo.split(":%")[2];
                    int temp = i + 1;
                    textViewText = "Email " + temp + ":\nReceived On: " + dateReceived + "\nSubject: " + subject + "\nBody(brief): " + body + "\n\n";
                    emailsArrayList.add(textViewText);
                }
                l1.setVisibility(View.VISIBLE);
            }
        }
    }
}