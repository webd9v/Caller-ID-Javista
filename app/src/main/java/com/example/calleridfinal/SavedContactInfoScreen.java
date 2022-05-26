package com.example.calleridfinal;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SavedContactInfoScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_contact_info_screen);
        Bundle bundle=this.getIntent().getExtras();
        String name=bundle.getString("name");
        String number=bundle.getString("phoneNumber");
    }
}
