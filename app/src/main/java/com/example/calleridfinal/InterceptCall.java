package com.example.calleridfinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InterceptCall extends BroadcastReceiver {
    private static final String TAG = "CustomBroadcastReceiver";
    static long start=0,end=0;
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            boolean isFound;


            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){

//                Toast.makeText(context,"Incoming Call State",Toast.LENGTH_SHORT).show();
//                Toast.makeText(context,"Ringing State Number is :"+incomingNumber,Toast.LENGTH_SHORT).show();

                if(incomingNumber!=null) {
                    try{
                        Date date=new Date();
                        this.start=date.getTime();
                        String contactInfo=MainActivity.contactsByPhone.get(incomingNumber);
                        System.out.println("++++++++++HashInfo from intercept:"+contactInfo);
                        System.out.println("+++++++start:"+start);


                        String phoneNumber=contactInfo.split(":")[0];
                        String address=contactInfo.split(":")[1];
                        String email=contactInfo.split(":")[2];
                        isFound=true;
                        Intent intent1 = new Intent(context, CallScreen.class);
                        intent1.putExtra("isFound",isFound);
                        intent1.putExtra("fullname",phoneNumber);
                        intent1.putExtra("phoneNumber", incomingNumber);
                        intent1.putExtra("address",address);
                        intent1.putExtra("email",email);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent1);
                    }catch (Exception e){
                        isFound=false;
                        Intent intent1 = new Intent(context, CallScreen.class);
                        intent1.putExtra("isFound",isFound);
                        intent1.putExtra("phoneNumber", incomingNumber);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent1);
                    }

                }
            }
//            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
//                Toast.makeText(context,"Call Received State",Toast.LENGTH_SHORT).show();
//            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                try {
                    if(MainActivity.contactsByPhone.get(incomingNumber)!=null) {
                        Date date = new Date();
                        this.end = date.getTime();
                        System.out.println("+++++++end:"+end);
                        long difference=end-start;
                        System.out.println("+++++++difference:"+difference);

                        double seconds=(double) difference/1000;
                        System.out.println("+++++++seconds:"+seconds);

                        double duration=(double) seconds/60;
                        System.out.println("+++++++duration:"+duration);

                        System.out.println("Duration:"+duration);
                        Intent intent1 = new Intent(context, AddCallScreen.class);
                        intent1.putExtra("duration",duration);
                        intent1.putExtra("phoneNumber", incomingNumber);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent1);
                    }

                }catch (Exception e){

                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
