package com.example.calleridfinal;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddContactScreen extends AppCompatActivity {
    final static String MSSCONTACTS_URL="https://apimd365.azure-api.net/api/contacts";
    ImageView exitCallScreen;
    Button addContactBtn;
    EditText firstnameField,lastnameField,emailField,phoneNumberField;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_screen);
        Bundle bundle=this.getIntent().getExtras();
        String number=bundle.getString("phoneNumber");
        phoneNumberField=findViewById(R.id.numberField);
        phoneNumberField.setText(number);
        exitCallScreen=findViewById(R.id.exitCallScreen);
        exitCallScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addContactBtn=findViewById(R.id.addContact);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstnameField=findViewById(R.id.firstnameField);
                lastnameField=findViewById(R.id.lastnameField);
                emailField=findViewById(R.id.emailField);

                String firstname=firstnameField.getText().toString();
                String lastname=lastnameField.getText().toString();
                String email=emailField.getText().toString();
                addContact(firstname,lastname,number,email);
                Toast.makeText(AddContactScreen.this,"Contact Added!",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    public void addContact(String firstname,String lastname,String phoneNumber,String email){
        RequestQueue queue= Volley.newRequestQueue(this);
//        JSONObject parameters=new JSONObject();
        JSONObject body=new JSONObject();
        try{
//            parameters.put("key","value");
            body.put("firstname",firstname);
            body.put("lastname",lastname);
            body.put("mobilephone",phoneNumber);
            body.put("emailaddress1",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, MSSCONTACTS_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error "+error.networkResponse);


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
                headers.put("Content-Type","application/json");
//                headers.put("Authorization","Bearer "+authenticationResult.getAccessToken());
//                headers.put("Accept","application/json");
//                headers.put("OData-MaxVersion","4.0");
//                headers.put("OData-Version","4.0");
//                headers.put("If-None-Match","null");
                headers.put("Host","apimd365.azure-api.net");
                headers.put("Ocp-Apim-Subscription-Key","ff217ee6bbf74c54972a77cf853a7436");
                headers.put("Ocp-Apim-Trace","true");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000*2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
