package com.example.calleridfinal;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class AddCallScreen extends AppCompatActivity {
    final static String MSSCALLS_URL="https://apimd365.azure-api.net/api/phonecalls";
    EditText phoneField,durationField,subjectField,descriptionField;
    Button addCall;
    TextView alertText;
    ImageView exitScreen;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_call_screen);
        Bundle bundle=this.getIntent().getExtras();
        double duration= (double) bundle.get("duration");
        String phoneNumber=bundle.getString("phoneNumber");
        phoneField=findViewById(R.id.numberField);
        phoneField.setText(phoneNumber);
        durationField=findViewById(R.id.duration);
        subjectField=findViewById(R.id.subject);
        descriptionField=findViewById(R.id.description);
        addCall=findViewById(R.id.addCall);
        alertText=findViewById(R.id.alertText);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        String durationString=df.format(duration) +" min";
        durationField.setText(durationString);

        exitScreen=findViewById(R.id.exitCallScreen);
        exitScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descriptionField.getText().toString()==null || subjectField.getText().toString()==null){
                    alertText.setVisibility(View.VISIBLE);
                }else{
                    String subject=subjectField.getText().toString();
                    String description=descriptionField.getText().toString();
                    addCalls(phoneNumber,durationString,subject,description);
                    Toast.makeText(AddCallScreen.this,"Call Added!",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

    }
//                            "scheduleddurationminutes": 9,
//                            "phonenumber": "614-555-0127",
//                            "actualdurationminutes": 9,
//                            "activitytypecode": "phonecall"
    public void addCalls(String phoneNumber, String duration,String subject,String description){
        RequestQueue queue= Volley.newRequestQueue(AddCallScreen.this);
//        JSONObject parameters=new JSONObject();
        JSONObject body=new JSONObject();
        try{
            body.put("phonenumber",phoneNumber);
            body.put("activitytypecode","phonecall");
            body.put("scheduleddurationminutes",duration);
            body.put("actualdurationminutes",duration);
            body.put("subject",subject);
            body.put("description",description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, MSSCALLS_URL, body, new Response.Listener<JSONObject>() {
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
